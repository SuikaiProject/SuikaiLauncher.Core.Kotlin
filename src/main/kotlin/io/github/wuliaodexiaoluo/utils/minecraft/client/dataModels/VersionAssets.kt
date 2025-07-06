package io.github.wuliaodexiaoluo.utils.minecraft.client.dataModels

data class VersionAssets(
    val objects:MutableMap<String,FileEntry>
)


data class FileEntry(
    val hash:String,
    val size:Long
)