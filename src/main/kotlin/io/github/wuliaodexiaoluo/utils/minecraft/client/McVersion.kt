package io.github.wuliaodexiaoluo.utils.minecraft.client

import io.github.wuliaodexiaoluo.utils.minecraft.client.dataModels.VersionJson
import io.github.wuliaodexiaoluo.utils.minecraft.runtimes.JavaProperty

class GameProfile{
    var versionIsolation:Boolean = false
    var requireJava:JavaProperty? = null
    var installPath:String = ""
    var launchCount:Long = -1L
    var hasSubassembly:Boolean = false
    var minecraftPath:String? = null
    var gameVersionName:String? = null
    companion object{
        fun getProfile(installPath:String):GameProfile{
            val profile = GameProfile()
            profile.installPath = installPath
            return profile
        }
    }
}


class McVersion (
    var versionName: String = "",
    var isSnapshot: Boolean = false,
    var isOldVersion: Boolean = false,
    var releaseTime: String = "",
    var jsonUrl: String = "",
    var jsonHash: String = "",
    var jsonData:String = "",
    var jsonClass:VersionJson?
){
    companion object{
        suspend fun lookup(gameVersion:String):McVersion?{
            if(DefaultSource.mcVersionList == null) DefaultSource.update()
            DefaultSource.mcVersionList?.versions?.forEach{version ->
                if(version.id == gameVersion) return McVersion(
                    version.id,
                    version.currentType == ReleaseType.Snapshot,
                    version.currentType == ReleaseType.Old
                )
            }
            return null
        }
    }
}