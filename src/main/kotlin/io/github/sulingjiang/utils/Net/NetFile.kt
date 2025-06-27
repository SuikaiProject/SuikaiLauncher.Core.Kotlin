package io.github.sulingjiang.utils.net

import kotlin.math.*

enum class NetFileState{

}

data class NetFile(
    val filePath: String,
    val fileSize: Long = -1L,
    val url:String,
    val hash:String
){
    //var State
    val minSize:Long = 1 * 1024 * 1024
    val maxSize:Long = 25 * 1024 * 1024
    fun splitFile():List<NetFile> {
        if(fileSize <= 0L) return listOf(this)
        if(min(fileSize,minSize) == fileSize) return listOf(this)
        if(max(fileSize,maxSize) > maxSize)
    }
}