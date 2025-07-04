package io.github.wuliaodexiaoluo.utils.minecraft.client

import io.github.wuliaodexiaoluo.utils.minecraft.runtimes.JavaProperty

data class McVersion (
    var gameVersionName:String = "",
    var majorVersion:Int = -1,
    var minorVersion: Int = -1,
    var patchVersion:Int = -1,
    var isSnapshot:Boolean = false,
    var isOldVersion:Boolean = false,
    var hasSubassembly:Boolean = false,
    var launchCount:Long = -1L,
    var requireJavaVersion: JavaProperty? = null,
    var jsonPath:String,
    var gamePath:String,
    var enableVersionIsolation:Boolean = true,
    var corePath:String = "",
    var jsonUrl:String = ""
){
        suspend fun lookup(){
            if(gameVersionName.isBlank()) throw IllegalArgumentException("gameVersionName 不能为空")
            if(DefaultSource.mcVersionList == null) DefaultSource.update()
            DefaultSource.mcVersionList?.versions?.forEach { version ->
                if(version.id == gameVersionName){
                    isSnapshot = version.description!!.contains("快照版")
                    isOldVersion = version.description!!.contains("远古版")
                    val versionId = version.type.split(".")
                    majorVersion = if(isOldVersion) 0 else if (isSnapshot) 2 else versionId[0].toInt()
                    minorVersion = versionId[1].toInt()
                    patchVersion = versionId[2].toInt()
                    jsonUrl = version.url
                }
            }
        }
        fun localLookup(localName: String){

        }
}