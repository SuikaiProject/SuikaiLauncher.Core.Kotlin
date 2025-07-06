package io.github.wuliaodexiaoluo.utils.minecraft.client

import io.github.shimoranla.utils.basic.FileIO
import io.github.shimoranla.utils.basic.Text
import io.github.shimoranla.utils.basic.net.HttpRequestOptions
import io.github.shimoranla.utils.basic.net.HttpWebRequest
import io.github.sulingjiang.utils.Net.Downloader
import io.github.sulingjiang.utils.net.NetFile
import io.github.wuliaodexiaoluo.utils.minecraft.client.dataModels.VersionAssets
import io.github.wuliaodexiaoluo.utils.minecraft.client.dataModels.VersionJson
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.Enumeration
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

class Client(
    var profile: GameProfile? = null,
    var mcVersion: McVersion? = null
) {
    /**
     * 将已有 Jar 文件注入到新的 Jar（并自动删除签名）
     * @param jarFile 要添加的 Jar File
     */
    fun addToCore(jarFile: String) {

        val tempPath =
            ("${profile?.minecraftPath}/versions/${profile?.gameVersionName}/" +
                    "${profile?.gameVersionName}.jar")
                .replace(".jar", ".jar_tmp")

        FileOutputStream(tempPath).use { fileStream ->
            BufferedOutputStream(fileStream, 16384).use { buffer ->
                JarOutputStream(buffer).use { out ->
                    processJarEntries(tempPath.replace(".jar_tmp",".jar"), out)
                    processJarEntries(jarFile, out)
                }
            }
        }

        Files.move(Paths.get(tempPath), Paths.get(tempPath.replace(".jar_tmp",".jar")),
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
        HttpWebRequest.getServerResponse(
            HttpRequestOptions(
                mcVersion?.jsonUrl ?: throw IllegalArgumentException("无效的文件下载地址"),
                "GET"
            )
        ).body.use { stream ->
            mcVersion?.jsonData =
                String(stream?.byteStream()?.readAllBytes()?:"".toByteArray(),StandardCharsets.UTF_8)
        }
        FileIO.getFileOutputStream("${profile?.installPath}/${profile?.gameVersionName}.json")
            .use{ stream ->
                stream.write(mcVersion?.jsonData?.toByteArray() ?: throw IllegalArgumentException("无效的 Json 文件"))
        }
        mcVersion?.jsonClass = Text.JsonResolver.fromJson(mcVersion?.jsonData, VersionJson::class.java)

    }
    suspend fun getMinecraftAssets(){
        val DataString:String
        HttpWebRequest.getServerResponse(
            HttpRequestOptions(
                mcVersion?.jsonClass?.assetsIndex?.url ?: throw IllegalArgumentException("无效的文件下载地址"),
                "GET"
            )
        ).body.use { stream ->
            DataString =
                String(stream?.byteStream()?.readAllBytes()?:"".toByteArray(),StandardCharsets.UTF_8)
            FileIO.getFileOutputStream("${profile?.minecraftPath}/assets/indexes/${mcVersion?.jsonClass?.assetsIndex?.id}.json")
                .use{ fileStream ->
                    fileStream.write(mcVersion?.jsonData?.toByteArray() ?: throw IllegalArgumentException("无效的 Json 文件"))
                }
        }
        val assetsIndex = Text.JsonResolver.fromJson(DataString,VersionAssets::class.java)
        assetsIndex.objects.forEach { data ->
            Downloader.DownloadAsync(NetFile(
                profile?.minecraftPath + "/assets/object/" + data.value.hash.substring(2) + data.value.hash,
                data.value.size,
            ))
        }
    }
    fun getMinecraftLibraries(){

    }
    fun fixMinecraftLibraries(){

    }
    fun fixMinecraftAssets(){

    }
    suspend fun install(){
        downloadVersionJson()
        fixMinecraftAssets()
        fixMinecraftLibraries()
    }
}
