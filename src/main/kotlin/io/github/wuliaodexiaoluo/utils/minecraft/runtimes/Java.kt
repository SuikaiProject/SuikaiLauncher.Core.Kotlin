package io.github.wuliaodexiaoluo.utils.minecraft.runtimes

import io.github.wuliaodexiaoluo.utils.minecraft.client.McVersion
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Java {
    companion object{
        fun selector(javaList:List<JavaProperty>,maxVersion:JavaProperty? = null,minVersion: JavaProperty? = null){
            val allowedJavaList: MutableList<JavaProperty> = mutableListOf()
            javaList.forEach { java ->
                if(minVersion != null) {
                    if (java.majorVersion < minVersion.majorVersion ||
                        java.minorVersion < minVersion.minorVersion ||
                        java.patchVersion < minVersion.patchVersion
                    ) return
                }
                if(maxVersion != null){
                    if (java.majorVersion > maxVersion.majorVersion ||
                        java.minorVersion > maxVersion.minorVersion ||
                        java.patchVersion > maxVersion.patchVersion
                    ) return
                }
                if(java.javaPath.contains("target",true) ||
                    java.javaPath.contains("java_path",true) ||
                    java.javaPath.contains("java8path",true)) return
                allowedJavaList.add(java)
            }
        }

        /**
         * 从 Java 列表选择最合适的 Java
         * @param javaList: 可选的 Java
         * @param mcVersion: 要启动的版本号
         */
        fun getBestJava(javaList: MutableList<JavaProperty>,mcVersion: McVersion? = null){
            javaList.filter { java ->
                // 1.优先选择与当前版本适配的 Java
                if(java.majorVersion == mcVersion?.requireJavaVersion?.majorVersion && java.vmType != JVMType.OpenJ9) return@filter true
                // 2.OpenJDK 通常优化更好
                if(java.javaType == JavaType.OpenJDK || java.vmType != JVMType.OpenJ9) return@filter true
                // 3.在有第三方组件时优先考虑 Hotspot 虚拟机
                if(java.vmType == JVMType.OpenJ9 && mcVersion?.hasSubassembly?:false) return@filter false
                // 4.如果是 Java 8 且不带附加组件，优先考虑 PatchVersion 更靠近 51 的
                if(mcVersion?.hasSubassembly?:false && mcVersion?.requireJavaVersion?.majorVersion == 8 && java.majorVersion == 8 && abs(java.patchVersion - 51) <= 60) return@filter true
                // 5.在没有附加组件的情况下考虑 OpenJ9
                if(!(mcVersion?.hasSubassembly?:false) && java.vmType == JVMType.OpenJ9) return@filter true
                // 6.排除路径带 target\path 字段的 Java
                if(java.javaPath.contains("target") || java.javaPath.contains("path") || java.javaPath.contains("java8path")) return@filter false
                return@filter false
            }
        }
    }
}