package io.github.wuliaodexiaoluo.utils.minecraft.client

import io.github.sulingjiang.utils.Net.Downloader
import io.github.sulingjiang.utils.net.NetFile
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.Enumeration
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

class Client(
    val mcVersion: McVersion
) {
    /**
     * 将已有 Jar 文件注入到新的 Jar（并自动删除签名）
     * @param jarFile 要添加的 Jar File
     */
    fun addToCore(jarFile: String) {

        val tempPath = mcVersion.corePath.replace(".jar", ".jat_tmp")

        FileOutputStream(tempPath).use { fileStream ->
            BufferedOutputStream(fileStream, 16384).use { buffer ->
                JarOutputStream(buffer).use { out ->
                    processJarEntries(mcVersion.corePath, out)
                    processJarEntries(jarFile, out)
                }
            }
        }

        Files.move(Paths.get(tempPath), Paths.get(mcVersion.corePath),
            StandardCopyOption.REPLACE_EXISTING)
    }
    private fun processJarEntries(jarPath: String, out: JarOutputStream) {
        JarFile(jarPath).use { jarFile ->
            jarFile.entries().toList().forEach { entry ->
                if (!isSignatureFile(entry.name)) {
                    out.putNextEntry(JarEntry(entry.name))
                    jarFile.getInputStream(entry).copyTo(out)
                    out.closeEntry()
                }
            }
        }
    }
    // Oracle JDK 会在有签名时验证文件签名，but 我们已经修改了 Jar 文件，所以原签名失效了
    // 这会导致加载 Jar 时抛出 Exception
    private fun isSignatureFile(name: String): Boolean {
        return name.startsWith("META-INF/")
    }
    suspend fun downloadVersionJson(){
        if(mcVersion.jsonUrl.isBlank()) {
            mcVersion.lookup()
        }
        Downloader

    }
}
