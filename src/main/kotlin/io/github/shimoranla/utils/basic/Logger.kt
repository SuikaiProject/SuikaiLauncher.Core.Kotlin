package io.github.shimoranla.utils.basic

import java.io.File
import java.io.FileOutputStream
import okio.Buffer
import java.time.LocalTime
import java.time.format.DateTimeFormatter

const val RED   = "\u001B[31m"
const val GREEN = "\u001B[32m"
const val YELLOW= "\u001B[33m"
const val BLUE  = "\u001B[34m"




class Logger{
    val fileStream = FileOutputStream(File("./LogTest.log"))

    private fun getCurrentTime(): String? {
        val now = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH.mm.ss.SSS")
        return now.format(formatter)
    }
    val LogStream = Buffer()

    fun debug(message:String){
        println("$BLUE [${getCurrentTime()}] | [Debug] | $message")
        LogStream.writeUtf8(message)
        LogStream.writeUtf8(message)
    }
    fun info(message: String){
        println("$GREEN [${getCurrentTime()}] | [Info] | $message")
        LogStream.writeUtf8(message)
    }
    fun warning(message: String){
        println("$YELLOW [${getCurrentTime()}] | [Warning] | $message")
        LogStream.writeUtf8(message)
    }
    fun error(message: String){
        println("$RED [${getCurrentTime()}] | [Error] | $message")
        LogStream.writeUtf8(message)
    }
    fun flush(){
        LogStream.copyTo(fileStream)
        fileStream.flush()
    }
}

val logger = Logger()

fun main(){
    logger.debug("龙泰于霜 烽烟硝长 更赳赳昂昂")
    logger.info("至少在这一刻 热爱不问为何")
    logger.warning("那张尾页的书签 那天带泪的笑颜 依然勇敢 依然耀眼")
    logger.error("浑天空垂影 司苍生无可探 看造化不停永循环")
    logger.flush()
}