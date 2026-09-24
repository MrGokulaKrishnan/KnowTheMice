package com.knowthemice.app.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

data class AndroidUpdateInfo(
    val version: String = "",
    val versionCode: Int = 0,
    val downloadUrl: String = "",
    val sha256: String = "",
    val changelog: String = "",
    val mandatory: Boolean = false
)

sealed class AndroidUpdateState {
    object Idle : AndroidUpdateState()
    object Checking : AndroidUpdateState()
    object UpToDate : AndroidUpdateState()
    data class UpdateAvailable(val info: AndroidUpdateInfo) : AndroidUpdateState()
    data class Downloading(val info: AndroidUpdateInfo, val progressPct: Int) : AndroidUpdateState()
    data class Ready(val info: AndroidUpdateInfo, val apkPath: String) : AndroidUpdateState()
    data class Failed(val error: String, val info: AndroidUpdateInfo? = null) : AndroidUpdateState()
}

object UpdateManager {
    const val METADATA_URL = "https://knowthemice.web.app/downloads.json"

    private val _updateState = MutableStateFlow<AndroidUpdateState>(AndroidUpdateState.Idle)
    val updateState: StateFlow<AndroidUpdateState> = _updateState.asStateFlow()

    fun initialize(context: Context, currentVersionCode: Int) {
        val prefs = context.getSharedPreferences("knowthemice_update", Context.MODE_PRIVATE)
        val stagedJson = prefs.getString("staged_update", null)

        if (!stagedJson.isNullOrBlank()) {
            try {
                val stagedObj = JSONObject(stagedJson)
                val version = stagedObj.optString("version", "")
                val versionCode = stagedObj.optInt("versionCode", 0)
                val apkPath = stagedObj.optString("apkPath", "")
                val sha256 = stagedObj.optString("sha256", "")
                val changelog = stagedObj.optString("changelog", "")
                val apkFile = File(apkPath)

                if (versionCode > currentVersionCode && apkFile.exists()) {
                    if (sha256.isBlank() || verifySha256(apkFile, sha256)) {
                        val info = AndroidUpdateInfo(
                            version = version,
                            versionCode = versionCode,
                            downloadUrl = "",
                            sha256 = sha256,
                            changelog = changelog
                        )
                        _updateState.value = AndroidUpdateState.Ready(info, apkPath)
                        return
                    }
                }

                // If running code >= staged code, or file missing/invalid, clean up
                if (apkFile.exists()) {
                    apkFile.delete()
                }
                prefs.edit().remove("staged_update").apply()
            } catch (_: Exception) {}
        }

        _updateState.value = AndroidUpdateState.Idle
    }

    suspend fun checkForUpdates(context: Context, currentVersionCode: Int) {
        // If an update is already staged, keep Ready state
        if (_updateState.value is AndroidUpdateState.Ready) {
            return
        }

        _updateState.value = AndroidUpdateState.Checking

        withContext(Dispatchers.IO) {
            var conn: HttpURLConnection? = null
            try {
                conn = openConnectionWithRedirects("$METADATA_URL?t=${System.currentTimeMillis()}")
                if (conn.responseCode == 200) {
                    val json = conn.inputStream.bufferedReader().use { it.readText() }
                    val root = JSONObject(json)
                    val androidObj = root.optJSONObject("android")

                    if (androidObj != null) {
                        val version = androidObj.optString("version", "")
                        val versionCode = androidObj.optInt("versionCode", 0)
                        val downloadUrl = if (androidObj.has("downloadUrl") && androidObj.getString("downloadUrl").isNotBlank()) {
                            androidObj.getString("downloadUrl")
                        } else {
                            androidObj.optString("url", "")
                        }
                        val sha256 = androidObj.optString("sha256", "")
                        val changelog = androidObj.optString("changelog", "")
                        val mandatory = androidObj.optBoolean("mandatory", false)

                        if (versionCode > currentVersionCode) {
                            val info = AndroidUpdateInfo(
                                version = version,
                                versionCode = versionCode,
                                downloadUrl = downloadUrl,
                                sha256 = sha256,
                                changelog = changelog,
                                mandatory = mandatory
                            )
                            _updateState.value = AndroidUpdateState.UpdateAvailable(info)
                            return@withContext
                        }
                    }
                }

                _updateState.value = AndroidUpdateState.UpToDate
            } catch (ex: Exception) {
                _updateState.value = AndroidUpdateState.Failed("Check failed: ${ex.message}")
            } finally {
                conn?.disconnect()
            }
        }
    }

    suspend fun downloadUpdate(context: Context, info: AndroidUpdateInfo) {
        _updateState.value = AndroidUpdateState.Downloading(info, 0)

        withContext(Dispatchers.IO) {
            var conn: HttpURLConnection? = null
            try {
                conn = openConnectionWithRedirects(info.downloadUrl)
                val totalBytes = conn.contentLength.toLong()
                val cacheDir = context.cacheDir
                val apkFile = File(cacheDir, "KnowTheMice-Update.apk")
                if (apkFile.exists()) {
                    apkFile.delete()
                }

                conn.inputStream.use { input ->
                    FileOutputStream(apkFile).use { output ->
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        var downloaded = 0L
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            downloaded += bytesRead
                            if (totalBytes > 0) {
                                val pct = ((downloaded * 100) / totalBytes).toInt()
                                _updateState.value = AndroidUpdateState.Downloading(info, pct)
                            }
                        }
                    }
                }

                // Verify SHA-256 if provided
                if (info.sha256.isNotBlank() && !verifySha256(apkFile, info.sha256)) {
                    apkFile.delete()
                    throw IllegalStateException("APK SHA-256 verification failed.")
                }

                // Persist staged update via JSON
                val stagedObj = JSONObject().apply {
                    put("version", info.version)
                    put("versionCode", info.versionCode)
                    put("apkPath", apkFile.absolutePath)
                    put("sha256", info.sha256)
                    put("changelog", info.changelog)
                }
                context.getSharedPreferences("knowthemice_update", Context.MODE_PRIVATE)
                    .edit()
                    .putString("staged_update", stagedObj.toString())
                    .apply()

                _updateState.value = AndroidUpdateState.Ready(info, apkFile.absolutePath)
            } catch (ex: Exception) {
                _updateState.value = AndroidUpdateState.Failed("Download error: ${ex.message}", info)
            } finally {
                conn?.disconnect()
            }
        }
    }

    private fun openConnectionWithRedirects(initialUrl: String): HttpURLConnection {
        var curUrl = initialUrl
        var redirects = 0
        while (redirects < 5) {
            val u = URL(curUrl)
            val conn = (u.openConnection() as HttpURLConnection).apply {
                connectTimeout = 15000
                readTimeout = 30000
                instanceFollowRedirects = true
                setRequestProperty("User-Agent", "KnowTheMice-Android/1.2.0")
                requestMethod = "GET"
            }
            val code = conn.responseCode
            if (code in 300..399) {
                val loc = conn.getHeaderField("Location")
                conn.disconnect()
                if (!loc.isNullOrBlank()) {
                    curUrl = if (loc.startsWith("http://") || loc.startsWith("https://")) loc else URL(u, loc).toString()
                    redirects++
                    continue
                }
            }
            if (code != 200) {
                conn.disconnect()
                throw IllegalStateException("Server returned HTTP $code: ${conn.responseMessage}")
            }
            return conn
        }
        throw IllegalStateException("Too many HTTP redirects.")
    }

    fun installStagedUpdate(context: Context) {
        val state = _updateState.value
        if (state !is AndroidUpdateState.Ready) return

        val apkFile = File(state.apkPath)
        if (!apkFile.exists()) {
            _updateState.value = AndroidUpdateState.Failed("Staged APK not found on disk.")
            return
        }

        try {
            val apkUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(installIntent)
        } catch (ex: Exception) {
            _updateState.value = AndroidUpdateState.Failed("Installation failed: ${ex.message}")
        }
    }

    private fun verifySha256(file: File, expectedHash: String): Boolean {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            file.inputStream().use { stream ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (stream.read(buffer).also { bytesRead = it } != -1) {
                    digest.update(buffer, 0, bytesRead)
                }
            }
            val hash = digest.digest().joinToString("") { "%02X".format(it) }
            hash.equals(expectedHash.replace("-", ""), ignoreCase = true)
        } catch (_: Exception) {
            false
        }
    }
}
