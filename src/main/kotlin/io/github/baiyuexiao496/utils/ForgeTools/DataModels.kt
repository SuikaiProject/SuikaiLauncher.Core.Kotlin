package io.github.baiyuexiao496.utils.ForgeTools

import com.google.gson.annotations.SerializedName

data class ForgeProfile(
    @SerializedName("_comment")
    val comments: List<String>,
    val hideExtract: Boolean,
    val spec:Int,
    val profile:String,
    val version:String,
    val path:String,
    val minecraft:String,
    val serverJarPath:String?//,
    //val
)

data class ForgeData(
    @SerializedName("MOJANGS")
    val mojang:Mojang
)

data class Mojang(
    val client:String,
    val server:String
)