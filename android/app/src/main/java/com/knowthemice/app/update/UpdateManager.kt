package com.knowthemice.app.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

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

object UpdateManager {
    const val METADATA_URL = "https://knowthemice.web.app/downloads.json"
    private val gson = Gson()

    suspend fun checkForUpdates(currentVersionCode: Int): AndroidUpdateInfo? {
        return withContext(Dispatchers.IO) {
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
                        return@withContext androidInfo
                    }
                }
            } catch (_: Exception) {}
            null
        }
    }

    suspend fun downloadAndInstallApk(
        context: Context,
        downloadUrl: String,
        onProgress: (Int) -> Unit
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(downloadUrl)
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
                                withContext(Dispatchers.Main) {
                                    onProgress(pct)
                                }
                            }
                        }
                    }
                }

                // Launch package installer via FileProvider
                withContext(Dispatchers.Main) {
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
                }
                Result.success(Unit)
            } catch (ex: Exception) {
                Result.failure(ex)
            }
        }
    }
}
