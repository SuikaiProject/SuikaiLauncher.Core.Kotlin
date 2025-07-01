package io.github.wuliaodexiaoluo.utils.minecraft.client

import io.github.wuliaodexiaoluo.utils.minecraft.runtimes.JavaProperty

data class McVersion (
    var gameVersionName:String = "",
    val majorVersion:Int = -1,
    val minorVersion: Int = -1,
    val patchVersion:Int = -1,
    val isSnapshot:Boolean = false,
    val isOldVersion:Boolean = false,
    val hasSubassembly:Boolean = false,
    var launchCount:Long = -1L,
    val requireJavaVersion: JavaProperty? = null,
    var jsonPath:String,
    var gamePath:String,
    var enableVersionIsolation:Boolean = true,
    var corePath:String = "",
    var jsonUrl:String = ""
){
        fun lookup(version: String){

        }
        fun localLookup(localName: String){

        }
}