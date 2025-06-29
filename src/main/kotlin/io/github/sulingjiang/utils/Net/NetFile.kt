package io.github.sulingjiang.utils.net

import kotlin.math.*

enum class NetFileState{
    Complete,
    Error,
    Processing,
    Pending
}


data class NetFile(
    val filePath: String,
    val fileSize: Long = -1L,
    val url:MutableList<String>,
    val hash:String,
    var starts:Long = 0L,
    var ends:Long = -1L
){
    var currentUrl: String = ""
    init{
        this.currentUrl = this.url.first()
        this.url.removeAt(0)
    }
    val failedSourceWithException:MutableMap<String,Exception> = mutableMapOf()

    companion object{
        val multipleDownloadBlocked: MutableList<String> = mutableListOf(
            "bmclapi",
            "openbmclapi",
            "edu.cn"
        )

        val minSize:Long = 1 * 1024 * 1024
    }

    fun splitFile():MutableList<NetFile> {
        if(fileSize <= 0L) return mutableListOf(this)
        if(multipleDownloadBlocked.any{ it in this.url})
        // 不对小于 1M 的文件进行多线程下载
        if(min(fileSize,minSize) == fileSize) return mutableListOf(this)
        val fileList:MutableList<NetFile> = mutableListOf()
        var fileSizeLeft:Long = this.fileSize
        while(true){
            if (fileSizeLeft <=0L) break
            if(fileSizeLeft < 768 * 1024) {
                fileList.add(
                    NetFile(
                        this.filePath + ".tmp_" + fileList.size.toString(),
                        fileSizeLeft,
                        this.url,
                        "",
                        this.fileSize - fileSizeLeft,
                        this.fileSize
                    )
                )
                break
            }
            fileList.add(
                NetFile(
                    this.filePath + ".tmp_" + fileList.size.toString(),
                    this.fileSize - fileSizeLeft,
                    this.url,
                    "",
                    1024 * 1024 * 2,
                    this.fileSize
                )
            )
            fileSizeLeft -= 1024 * 1024 * 2

        }
        return fileList
    }
    fun onFiled(ex:Exception){

    }
}