package io.github.shimoranla.utils.basic

class LauncherInfo {
    companion object{
        var LauncherName = "SuikaiLauncher.Core"
        const val LauncherCoreVersion = "0.0.1"
        const val LauncherCoreVersionCode = 1
        var LauncherVersion = "0.0.1"
        const val LauncherCoreName = "SuikaiLauncher.Core"
        val dataPath = ((if(System.getProperty("os.name").contains("win",true))
                            System.getenv("APPDATA")
                        else System.getProperty("user.home")) + "/" + "SuikaiLauncher/Core/Data")
            .replace("\\","/").replace("//","/")
    }
}