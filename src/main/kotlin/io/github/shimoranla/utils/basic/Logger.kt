package io.github.shimoranla.utils.basic

import java.io.File
import java.time.LocalTime
import java.time.format.DateTimeFormatter

const val RED   = "\u001B[31m"
const val GREEN = "\u001B[32m"
const val YELLOW= "\u001B[33m"
const val BLUE  = "\u001B[34m"




class Logger{
    private val logFile = File("./LogTest.log").apply {
        if (!exists()) createNewFile()
    }
    val fileStream = logFile.bufferedWriter()
    private fun getCurrentTime(): String? {
        val now = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH.mm.ss.SSS")
        return now.format(formatter)
    }

    fun debug(message:String){
        val LogMsg = "$BLUE [${getCurrentTime()}] | [Debug] | $message\n"
        print(LogMsg)
        fileStream.write(LogMsg.replace(BLUE + " ",""))
    }
    fun info(message: String){
        val LogMsg ="$GREEN [${getCurrentTime()}] | [Info] | $message\n"
        print(LogMsg)
        fileStream.write(LogMsg.replace(GREEN + "",""))
    }
    fun warning(message: String){
        val LogMsg = "$YELLOW [${getCurrentTime()}] | [Warning] | $message\n"
        print(LogMsg)
        fileStream.write(LogMsg.replace(YELLOW + " ",""))
    }
    fun error(message: String){
        val LogMsg = "$RED [${getCurrentTime()}] | [Error] | $message\n"
        print(LogMsg)
        fileStream.write(LogMsg.replace(RED + " ",""))
    }
    fun warning(ex: Throwable,message:String){
        val exc = Exception("洛天依",ex)
        val stack = Tracker.getExceptionSummary(exc)
        val LogMsg = message + "\n\n详细信息:\n\n" + stack
        warning(LogMsg)
    }
    fun error(ex: Throwable,message:String){
        val exc = Exception("乐正绫",ex)
        val stack = Tracker.getExceptionSummary(exc)
        val LogMsg = message + "\n\n详细信息:\n\n" + stack
        error(LogMsg)
    }
    fun flush(){
        fileStream.flush()
    }
}

val logger = Logger()
