package com.knowthemice.app.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

data class UpdateMetadata(
    val latestVersion: String = "",
    val releaseDate: String = "",
    val android: AndroidUpdateInfo? = null
)

data class AndroidUpdateInfo(
    val version: String = "",
    val versionCode: Int = 0,
    val downloadUrl: String = "",
    val sha256: String = "",
    val changelog: String = "",
    val mandatory: Boolean = false
)

data class StagedAndroidInfo(
    val version: String = "",
    val versionCode: Int = 0,
    val apkPath: String = "",
    val sha256: String = "",
    val changelog: String = ""
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
    private val gson = Gson()

    private val _updateState = MutableStateFlow<AndroidUpdateState>(AndroidUpdateState.Idle)
    val updateState: StateFlow<AndroidUpdateState> = _updateState.asStateFlow()

    fun initialize(context: Context, currentVersionCode: Int) {
        val prefs = context.getSharedPreferences("knowthemice_update", Context.MODE_PRIVATE)
        val stagedJson = prefs.getString("staged_update", null)

        if (!stagedJson.isNullOrBlank()) {
            try {
                val staged = gson.fromJson(stagedJson, StagedAndroidInfo::class.java)
                val apkFile = File(staged.apkPath)

                if (staged.versionCode > currentVersionCode && apkFile.exists()) {
                    if (staged.sha256.isBlank() || verifySha256(apkFile, staged.sha256)) {
                        val info = AndroidUpdateInfo(
                            version = staged.version,
                            versionCode = staged.versionCode,
                            downloadUrl = "",
                            sha256 = staged.sha256,
                            changelog = staged.changelog
                        )
                        _updateState.value = AndroidUpdateState.Ready(info, staged.apkPath)
                        return
                    }
                }

                // If running code >= staged code, or file missing/invalid, clean up
                apkFile.delete()
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
            try {
                val url = URL("$METADATA_URL?t=${System.currentTimeMillis()}")
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 8000
                    readTimeout = 8000
                    requestMethod = "GET"
                }

                if (conn.responseCode == 200) {
                    val json = conn.inputStream.bufferedReader().use { it.readText() }
                    val meta = gson.fromJson(json, UpdateMetadata::class.java)
                    val androidInfo = meta.android

                    if (androidInfo != null && androidInfo.versionCode > currentVersionCode) {
                        _updateState.value = AndroidUpdateState.UpdateAvailable(androidInfo)
                        return@withContext
                    }
                }

                _updateState.value = AndroidUpdateState.UpToDate
            } catch (ex: Exception) {
                _updateState.value = AndroidUpdateState.Failed("Check failed: ${ex.message}")
            }
        }
    }

    suspend fun downloadUpdate(context: Context, info: AndroidUpdateInfo) {
        _updateState.value = AndroidUpdateState.Downloading(info, 0)

        withContext(Dispatchers.IO) {
            try {
                val url = URL(info.downloadUrl)
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 15000
                    readTimeout = 30000
                    instanceFollowRedirects = true
                    requestMethod = "GET"
                }

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

                // Verify SHA-256
                if (info.sha256.isNotBlank() && !verifySha256(apkFile, info.sha256)) {
                    apkFile.delete()
                    throw IllegalStateException("APK SHA-256 verification failed.")
                }

                // Persist staged update
                val staged = StagedAndroidInfo(
                    version = info.version,
                    versionCode = info.versionCode,
                    apkPath = apkFile.absolutePath,
                    sha256 = info.sha256,
                    changelog = info.changelog
                )
                context.getSharedPreferences("knowthemice_update", Context.MODE_PRIVATE)
                    .edit()
                    .putString("staged_update", gson.toJson(staged))
                    .apply()

                _updateState.value = AndroidUpdateState.Ready(info, apkFile.absolutePath)
            } catch (ex: Exception) {
                _updateState.value = AndroidUpdateState.Failed("Download error: ${ex.message}", info)
            }
        }
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
