plugins {
    kotlin("jvm") version "2.1.21"
}

group = "top.luotianyi0712.suikaiproject.launcher.core"
version = "1.0-SNAPSHOT"

repositories {
    // 国内镜像源（按访问速度排序）
    maven("https://maven.aliyun.com/repository/public/")  // 阿里云
    maven("https://repo.huaweicloud.com/repository/maven/") // 华为云
    maven {
        url = uri("https://jitpack.io")

        // You can ensure that only this library is loaded from JitPack, and nothing else, using "repository content filtering": https://docs.gradle.org/current/userguide/declaring_repositories.html#sec:repository-content-filtering
        content {
            includeGroup("com.github.nmalkin")
        }
    }
    maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/") // 腾讯云镜像站
    maven("https://repo1.maven.org/maven2/") // Maven Central 官方源（备用）
    mavenCentral()  // 保留原始源
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.microsoft.azure:msal4j:1.21.0")
    implementation("org.apache.maven:maven-artifact:3.9.6")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(11)
}