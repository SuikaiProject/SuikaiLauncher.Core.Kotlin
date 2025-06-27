package io.github.shimoranla.utils.basic

class Setup {
    val setup: MutableMap<String, MutableMap<String,Any>> = mutableMapOf(
        "Global" to mutableMapOf(
            "DisableProxy" to false,
            "ProxyServer" to "",
            "ProxyPort" to "",
            "ProxyUser" to "",
            "ProxyPassword" to "",
            "JvmArguments" to "",
            "OverrideMetaRepository" to "",
            "PreferVersionSource" to SetupValue.Auto,
            "PreferDownloadSource" to SetupValue.Auto,
            "DisableMessageReport" to false,
            "VersionIsolation" to true,
            "GameArgument" to ""
        ),
        "Version" to mutableMapOf(
            "VersionIsolation" to true,
            "UseProxy" to true,
            "DisableMessageReport" to false,
            "ProxyServer" to "",
            "ProxyPort" to "",
            "ProxyUser" to "",
            "ProxyPassword" to "",
            "UseLauncherWrapper" to true,
            "AuthorizeServer" to "",
            "AuthorizeMethod" to SetupValue.All,
            "ReleaseMode" to false,
            "JvmArguments" to "",
            "GameArgument" to "",
            "ValidateFile" to true,
            "RequiredJavaVersion" to "",
            "SelectedJavaVersion" to "",
            "DurationOfPlay" to 0L,
            "MaxMemory" to SetupValue.Auto,
            "ModAble" to false,
            "VersionGC" to SetupValue.G1GC
        )
    )
}

enum class SetupValue{
    Auto,
    Offline,
    Microsoft,
    NideAuth,
    AuthLib,
    All,
    G1GC
}