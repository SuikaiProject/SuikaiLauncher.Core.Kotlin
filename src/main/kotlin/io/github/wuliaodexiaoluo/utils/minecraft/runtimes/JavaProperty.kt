package io.github.wuliaodexiaoluo.utils.minecraft.runtimes

enum class JavaType{
    Unknown,
    OpenJDK,
    OracleJDK,
}

enum class Issuer{
    Unknown,
    Microsoft,
    Oracle,
    IBM,
    Adoptium,
    Alibaba,
    Azul,
    OracleOpenJDK,
    BellSoft,
    Jetbrains
}

enum class JVMType {
    Unknown,
    Hotspot,
    OpenJ9
}

data class JavaProperty (
    var majorVersion:Int = -1,
    var minorVersion:Int = -1,
    var patchVersion:Int = -1,
    var issuer:Issuer = Issuer.Unknown,
    var javaType: JavaType = JavaType.Unknown,
    var vmType: JVMType = JVMType.Unknown,
    var addAt:String = "",
    var isEnable:Boolean = true,
    var javaPath:String = "",
    var javawPath:String = "",
    var downloadUrl:String
)
