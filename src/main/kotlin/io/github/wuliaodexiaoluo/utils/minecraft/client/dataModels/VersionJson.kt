package io.github.wuliaodexiaoluo.utils.minecraft.client.dataModels

import com.google.gson.annotations.SerializedName

data class VersionJson(
    val arguments: Arguments,
    val assetsIndex: AssetsIndex,
    val assets: String,
    val complianceLevel:Int,
    val downloads: CoreDownloads,
    val id: String?,
    val javaVersion: JavaVersion,
    val libraries: List<Libraries>,
    val logging: Logging,
    val mainClass: String,
    val minecraftArguments: String?,
    val minimumLauncherVersion:Int,
    val releaseTime: String,
    val time: String,
    val type: String
)

data class Downloads(
    var path: String?,
    val sha1: String,
    val size: Long,
    val url: String
)

data class Libraries(
    val downloads: LibrariesDownloads,
    val name: String,
    val rules: List<Rule>
)

data class OS(
    val name: String
)

data class Rule(
    val action:String,
    val os: OS
)

data class LibrariesDownloads(
    val artifact: Downloads,
    val classifiers: Map<String,Downloads?>
)

data class CoreDownloads(
    val client: Downloads,
    @SerializedName("client_mappings") val clientMappings: Downloads?,
    val server: Downloads?,
    @SerializedName("server_mappings") val serverMappings: Downloads?
)

data class Arguments(
    val game: Any?
)

data class JavaVersion(
    val component:String,
    val majorVersion:Int
)

data class AssetsIndex(
    val id:String,
    val sha1:String,
    val size:Long,
    val totalSize:Long,
    val url:String
)

data class Logging(
    val client: Client
)

data class Client(
    val argument: String,
    val file: LoggingFile,
    val type: String
)

data class LoggingFile(
    val id: String,
    val sha1: String,
    val size: Long,
    val url: String
)