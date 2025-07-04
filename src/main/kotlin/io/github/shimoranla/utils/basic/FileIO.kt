package io.github.shimoranla.utils.basic

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.extension

class FileIO {
    companion object {
        fun getFileOutputStream(filePath: String, bufferSize: Int = 16384): BufferedOutputStream {
            return BufferedOutputStream(FileOutputStream(filePath), bufferSize)
        }

        fun getFileInputStream(filePath: String, bufferSize: Int = 16384): BufferedInputStream {
            return BufferedInputStream(FileInputStream(filePath), bufferSize)
        }

        suspend fun getEnumerableFileList(filePath: String) {
            withContext(Dispatchers.IO) {
                val filesList = mutableListOf<Path>()
                Files.walkFileTree(Paths.get(filePath), object : SimpleFileVisitor<Path>() {

                    override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
                        return FileVisitResult.CONTINUE
                    }

                    override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {

                        filesList.add(file)
                        return FileVisitResult.CONTINUE
                    }

                    override fun visitFileFailed(file: Path, exc: IOException?): FileVisitResult {
                        if (exc == null) logger.error("[System] 遍历文件失败: $file")
                        else logger.error(exc, "[System] 遍历文件失败: $file")
                        return FileVisitResult.CONTINUE
                    }

                    override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
                        return FileVisitResult.CONTINUE
                    }
                })
            }
        }
    }
}