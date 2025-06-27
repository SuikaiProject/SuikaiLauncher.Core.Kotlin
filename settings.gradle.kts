pluginManagement {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public/") } // 阿里云
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin/") } // 阿里云Gradle插件仓库
        mavenCentral()
        gradlePluginPortal() // 官方的插件门户（plugins.gradle.org）的镜像，如果上面阿里云已经有了gradle-plugin，这个可以省略
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "SuikaiLauncher.Core"