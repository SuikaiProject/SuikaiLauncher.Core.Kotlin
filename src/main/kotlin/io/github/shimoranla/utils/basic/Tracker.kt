package io.github.shimoranla.utils.basic

import java.io.PrintWriter
import java.io.StringWriter

class Tracker {
    companion object{
        fun getExceptionSummary(ex:Exception):String{
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            ex.printStackTrace(pw)
            return sw.toString()
        }
    }
}