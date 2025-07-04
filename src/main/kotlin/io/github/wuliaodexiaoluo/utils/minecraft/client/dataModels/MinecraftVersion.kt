package io.github.wuliaodexiaoluo.utils.minecraft.client.dataModels

import java.time.LocalDate
import java.time.LocalDateTime

data class MinecraftVersion (
    val latest: Latest,
    val versions: MutableList<Versions>
)

data class Latest(
    val release:String?,
    val snapshot:String?
)
data class Versions(
    val id:String,
    var type:String,
    val url:String,
    val time:String,
    val releaseTime:String,
    val sha1:String?,
    val complianceLevel: Int?,
    // 内部使用字段

    // 用于存储版本描述
    var description:String?,
    // 存储格式化后的时间
    var releaseAt: LocalDateTime?
)