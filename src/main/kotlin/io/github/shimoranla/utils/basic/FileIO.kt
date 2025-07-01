package io.github.shimoranla.utils.basic

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream

class FileIO {
    companion object{
        fun getFileOutputStream(filePath:String,bufferSize:Int = 16384): BufferedOutputStream{
            return BufferedOutputStream(FileOutputStream(filePath),bufferSize)
        }
        fun getFileInputStream(filePath:String,bufferSize:Int = 16384): BufferedInputStream{
            return BufferedInputStream(FileInputStream(filePath),bufferSize)
        }
    }
}