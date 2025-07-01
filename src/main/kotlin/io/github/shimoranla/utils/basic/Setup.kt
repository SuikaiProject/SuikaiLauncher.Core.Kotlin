package io.github.shimoranla.utils.basic

import java.util.Properties

class Setup {
    companion object{
        fun load(setupFile:String): Properties{
            val prop = Properties()
            FileIO.getFileInputStream(setupFile).use { stream ->
                prop.load(stream)
            }
            return prop
        }
        fun save(prop: Properties, setupFile: String){
            FileIO.getFileOutputStream(setupFile).use{ stream ->
                prop.store(stream,"Update")
            }

        }
    }
}