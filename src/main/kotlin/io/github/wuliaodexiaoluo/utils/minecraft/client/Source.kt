package io.github.wuliaodexiaoluo.utils.minecraft.client

import io.github.shimoranla.utils.basic.FileIO
import io.github.shimoranla.utils.basic.LauncherInfo
import io.github.shimoranla.utils.basic.Setup
import io.github.shimoranla.utils.basic.Text
import io.github.shimoranla.utils.basic.logger
import io.github.shimoranla.utils.basic.net.HttpRequestOptions
import io.github.shimoranla.utils.basic.net.HttpWebRequest
import io.github.wuliaodexiaoluo.utils.minecraft.client.dataModels.MinecraftVersion
import io.github.wuliaodexiaoluo.utils.minecraft.client.dataModels.Versions
import kotlinx.coroutines.*
import java.nio.charset.StandardCharsets
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Date

enum class ReleaseType{
    Unknown,
    Release,
    Snapshot,
    Old,
    Fool
}

data class Source(
    val clientV1:String? = null,
    val clientV2:String? = null,
    val javaList:String? = null,
    val resourceServer:String? = null,
    val mavenServer:String? = null,
    val repositoryName:String,
    var isDisable: Boolean = false,
    var isSelected:Boolean = false,
){
    suspend fun downloadList(cacheStatus:MutableMap<String,Boolean>){
        val mcStatus = cacheStatus["Minecraft"]!!
        val javaStatus = cacheStatus["Java"]!!
        if(mcStatus || javaStatus) return
        withContext(Dispatchers.IO){
            if(!mcStatus && (clientV2.isNullOrBlank() || !clientV1.isNullOrBlank())) HttpWebRequest.getServerResponse(
                HttpRequestOptions(
                    clientV2?:clientV1!!,
                    "GET"
                )
            ).body?.byteStream().use{ stream ->
                if(stream == null) return@use
                FileIO.getFileOutputStream(LauncherInfo.dataPath + "/Repository/$repositoryName/mc.list.json")
                    .use{ filestream ->
                        stream.copyTo(filestream)
                    }

            }
        }
    }
    suspend fun checkCacheAvailable(){
        withContext(Dispatchers.IO){
            try {
                logger.info("[Source] 开始检查版本列表更新")
                val repositoryCache = Setup.load("$repositoryName.conf")
                val clientUrl = clientV2 ?: clientV1 ?: ""
                val javaUrl = javaList ?: ""
                val cacheStatus = mutableMapOf<String, Boolean>(
                    "Minecraft" to false,
                    "Java" to false
                )
                val clientETag = repositoryCache.getProperty("Minecraft.Version.ETag")
                val javaETag = repositoryCache.getProperty("Minecraft.Java.ETag")
                logger.debug("[Source] 缓存信息:\nJava 列表 ETag: $javaETag\nMinecraft 版本列表 ETag: $clientETag")
                if (javaUrl.isNotBlank()) {
                    val response = HttpWebRequest.getServerResponse(
                        HttpRequestOptions(
                            javaUrl,
                            "HEAD",
                            mutableMapOf(
                                "If-None-Match" to javaETag
                            )
                        )
                    )
                    if (response.code == 304) cacheStatus["Java"] = true
                    else repositoryCache.setProperty("Minecraft.Java.ETag",response.headers["ETag"]?:javaETag)
                }
                if (clientUrl.isNotBlank()) {
                    val response = HttpWebRequest.getServerResponse(
                        HttpRequestOptions(
                            clientUrl,
                            "HEAD",
                            mutableMapOf(
                                "If-None-Match" to clientETag
                            )
                        )
                    )
                    if(response.code == 304) cacheStatus["Minecraft"] = true
                    else repositoryCache.setProperty("Minecraft.Version.ETag",response.headers["ETag"]?:clientETag)
                }
                downloadList(cacheStatus)
            }catch (ex: Exception){
            }
        }
    }
    suspend fun loadSourceList():String{
        withContext(Dispatchers.IO){
            FileIO.getFileInputStream(LauncherInfo.dataPath + "/Repository/$repositoryName/mc.list.json")
                .use{stream -> return@withContext String(stream.readAllBytes(), StandardCharsets.UTF_8)}
        }
        return ""
    }
}

class DefaultSource {
    companion object {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX")
        val mojang: Source = Source(
            "https://piston-meta.mojang.com/mc/game/version_manifest.json",
            "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json",
            "https://launchermeta.mojang.com/v1/products/java-runtime/2ec0cc96c44e5a76b9c8b7c39df7210883d12871/all.json",
            "https://resources.download.minecraft.net",
            "https://libraries.minecraft.net",
            "Mojang"
        )
        val bmclAPI: Source = Source(
            "https://bmclapi2.bangbang93.com/mc/game/version_manifest.json",
            "https://bmclapi2.bangbang93.com/mc/game/version_manifest_v2.json",
            "https://piston-meta.mojang.com/v1/products/java-runtime/2ec0cc96c44e5a76b9c8b7c39df7210883d12871/all.json",
            "https://bmclapi2.bangbang93.com/assets",
            "https://bmclapi2.bangbang93.com/maven",
            "BMCLAPI"
        )
        val userCustomSource: MutableList<Source> = mutableListOf<Source>()
        fun getFoolDescription(versionId: String): String {
            return when (versionId.lowercase()) {
                "15w14a" -> "作为一款全年龄向的游戏，我们需要和平，需要爱与拥抱。"
                "1.rv-pre1" -> "是时候将现代科技带入 Minecraft 了！"
                "3d shareware v1.34" -> "我们从地下室的废墟里找到了这个开发于 1994 年的杰作！"
                "22w13oneblockatatime" -> "一次一个方块更新！迎接全新的挖掘、合成与骑乘玩法吧！"
                "23w13a_or_b" -> "研究表明：玩家喜欢作出选择——越多越好！"
                "24w14potato" -> "毒马铃薯一直都被大家忽视和低估，于是我们超级加强了它！"
                "25w14crafttime" -> "你可以合成任何东西——包括合成你的世界！"
                else -> {
                    if (versionId.startsWith("2.0")) "这个秘密计划了两年的更新将游戏推向了一个新高度！"
                    else if (versionId.lowercase().contains("20w14inf") ||
                        versionId.contains("∞")
                    ) "我们加入了 20 亿个新的维度，让无限的想象变成了现实！"
                    else ""
                }
            }
        }

        fun resolveVersion(versionList: MutableList<Versions>) {
            versionList.forEach { version ->
                version.releaseAt = OffsetDateTime.parse(version.releaseTime, formatter).toLocalDateTime()
                when (version.type) {
                    "release" -> {
                        version.description = "${version.releaseAt.toString()} | 正式版"
                        version.currentType = ReleaseType.Release
                    }
                    "snapshot" -> {
                        when (version.releaseAt?.format(DateTimeFormatter.ofPattern("MM-dd"))) {
                            null -> {
                                version.description = "未知版本"
                                version.currentType = ReleaseType.Unknown
                            }
                            "04-01" -> {
                                version.description =
                                    "${version.releaseAt?.year} | ${getFoolDescription(version.id)}"
                                version.currentType = ReleaseType.Fool
                            }
                            else -> {
                                version.description = "${version.releaseAt.toString()} | 快照版 "
                                version.currentType = ReleaseType.Snapshot
                            }
                        }
                    }

                    "old_beta", "old_alpha" -> {
                        version.description = "${version.releaseAt.toString()} | 远古版"
                        version.currentType = ReleaseType.Old
                    }
                }
            }
        }

        suspend fun update() {

            // 检查缓存是否可用（会自动更新文件）
            mojang.checkCacheAvailable()
            bmclAPI.checkCacheAvailable()
            userCustomSource.forEach { source ->
                source.checkCacheAvailable()
            }
            val mojangResult: MinecraftVersion = Text.JsonResolver.fromJson(
                mojang.loadSourceList(),
                MinecraftVersion::class.java
            )
            val bmclapiResult: MinecraftVersion =
                Text.JsonResolver.fromJson(bmclAPI.loadSourceList(), MinecraftVersion::class.java)

            // 合并版本列表
            mojangResult.versions.addAll(bmclapiResult.versions)
            userCustomSource.forEach { source ->
                mojangResult.versions.addAll(
                    Text.JsonResolver
                        .fromJson(
                            source.loadSourceList(),
                            MinecraftVersion::class.java
                        )
                        .versions
                )
            }
            // 对版本列表进行去重和排序，否则可能出现重复版本和新版本排在远古版本后面的情况
            mojangResult.versions.distinctBy { it.id }
            mojangResult.versions.sortByDescending { it ->
                try {
                    OffsetDateTime.parse(it.releaseTime)
                } catch (ex: DateTimeParseException) {
                    logger.error(ex, "[Minecraft] 格式化版本日期失败")
                }
                OffsetDateTime.MIN
            }
            resolveVersion(mojangResult.versions)
            mcVersionList = mojangResult
        }

        var mcVersionList: MinecraftVersion? = null
    }

    /**
     * 获取资源文件的下载列表
     * @param
     */
    fun getAssetsDlSource(url:String):MutableList<String>{
        val downloadUrls:MutableList<String> = mutableListOf()
        downloadUrls.add(url)
        downloadUrls.add(url.replace(mojang.resourceServer!!,bmclAPI.resourceServer!!))
        userCustomSource.forEach{ source ->
            if(source.resourceServer != null){
                downloadUrls.add(url.replace(mojang.resourceServer,source.resourceServer))
            }
        }
        return downloadUrls
    }
    fun getLibrariesDlSource(url:String):MutableList<String>{
        val fixedUrl = url.replace(
            "launcher.mojang.com","piston-meta.mojang.com"
        ).replace(
            "launchermeta.mojang.com","piston-meta.mojang.com"
        )
        val downloadUrls:MutableList<String> = mutableListOf(fixedUrl)
        downloadUrls.add(fixedUrl.replace(mojang.mavenServer!!, bmclAPI.mavenServer!!)
            .replace("piston-meta.mojang.com","bmclapi2.bangbang93.com")
            .replace("piston-data.mojang.com","bmclapi2.bangbang93.com"))
        userCustomSource.forEach{source ->
            if (source.mavenServer != null){
                downloadUrls.add(fixedUrl.replace(mojang.mavenServer, source.mavenServer)
                    .replace("piston-meta.mojang.com",source.mavenServer)
                    .replace("piston-data.mojang.com",source.mavenServer))
            }
        }
        return downloadUrls
    }
}

