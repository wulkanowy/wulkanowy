package io.github.wulkanowy.data.repositories.logger

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject

class LoggerRepository @Inject constructor(private val context: Context) {

    suspend fun getLastLogLines(): List<String> {
        return getLastModified().readText().split("\n")
    }

    suspend fun getLogFiles(): List<File> {
        return withContext(Dispatchers.IO) {
            File(context.filesDir.absolutePath).listFiles(File::isFile)?.filter {
                it.name.endsWith(".log")
            }!!
        }
    }

    private suspend fun getLastModified(): File {
        return withContext(Dispatchers.IO) {
            var lastModifiedTime = Long.MIN_VALUE
            var chosenFile: File? = null
            File(context.filesDir.absolutePath).listFiles(File::isFile)?.forEach { file ->
                if (file.lastModified() > lastModifiedTime) {
                    lastModifiedTime = file.lastModified()
                    chosenFile = file
                }
            }
            if (chosenFile == null) throw FileNotFoundException("Log file not found")
            chosenFile!!
        }
    }
}
