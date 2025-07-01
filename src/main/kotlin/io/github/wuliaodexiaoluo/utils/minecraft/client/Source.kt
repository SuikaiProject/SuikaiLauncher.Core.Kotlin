package io.github.wuliaodexiaoluo.utils.minecraft.client

import io.github.shimoranla.utils.basic.LauncherInfo
import io.github.shimoranla.utils.basic.Setup
import io.github.shimoranla.utils.basic.net.HttpRequestOptions
import io.github.shimoranla.utils.basic.net.HttpWebRequest
import kotlinx.coroutines.*
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.util.Properties
data class Source(
    var clientV1:String? = null,
    var clientV2:String? = null,
    var javaList:String? = null,
    var resourceServer:String? = null,
    var mavenServer:String? = null,
    val repositoryName:String,
    var isDisable: Boolean = false,
    var isSelected:Boolean = false,
){
    suspend fun checkUpdate(){
        withContext(Dispatchers.IO){
            try{
                val repostioryCache = Setup.load("$repositoryName.conf")
                val clientUrl = clientV2 ?: clientV1 ?: ""
                val javaUrl = javaList?:""
                if(!javaUrl.isBlank()) {

                    HttpWebRequest.getServerResponse(
                        HttpRequestOptions(
                            javaUrl,
                            "GET"
                        )
                    )
                }
                if(!clientUrl.isBlank()) HttpWebRequest.getServerResponse(
                    HttpRequestOptions(
                        javaUrl,
                        "GET"
                    )
                )
        }catch (ex: Exception){
        }
        }
    }
}

class DefaultSource {
    val mojang:Source = Source(
        "https://piston-meta.mojang.com/mc/game/version_manifest.json",
        "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json",
        "https://launchermeta.mojang.com/v1/products/java-runtime/2ec0cc96c44e5a76b9c8b7c39df7210883d12871/all.json",
        "https://resources.download.minecraft.net",
        "https://libraries.minecraft.net",
        "Mojang"
    )
    val bmclAPI:Source = Source(
        "https://bmclapi2.bangbang93.com/mc/game/version_manifest.json",
        "https://bmclapi2.bangbang93.com/mc/game/version_manifest_v2.json",
        "https://piston-meta.mojang.com/v1/products/java-runtime/2ec0cc96c44e5a76b9c8b7c39df7210883d12871/all.json",
        "https://bmclapi2.bangbang93.com/assets",
        "https://bmclapi2.bangbang93.com/maven",
        "BMCLAPI")
    val userCustomSource: MutableList<Source> = mutableListOf<Source>()
    suspend fun update(){
        mojang.checkUpdate()
        bmclAPI.checkUpdate()
        userCustomSource.forEach { source ->
            source.checkUpdate()
        }
    }
}

