package io.github.shimoranla.utils.basic.net

import io.github.shimoranla.utils.basic.LauncherInfo
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okio.BufferedSink
import okio.Buffer
import java.io.FileInputStream
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

val Client: OkHttpClient = OkHttpClient.Builder()
    .connectionPool(ConnectionPool(1024, 5L, TimeUnit.MINUTES))
    .build()


class HttpProxy {
    // You can add proxy logic here if needed in the future.
}

class StreamRequestBody(
    private val mediaType: String = "application/json",
    private val requestStream: Buffer?,
    private val contentLength: Long
) : RequestBody() {
    override fun contentLength(): Long = contentLength

    override fun contentType(): MediaType? = mediaType.toMediaType()

    override fun writeTo(sink: BufferedSink) {
        if (requestStream == null) {
            sink.writeUtf8("")
            return
        }
        sink.writeAll(requestStream.clone()) // Use clone to avoid closing original buffer
    }
}

data class HttpRequestOptions(
    val url: String,
    val method: String,
    var headers: MutableMap<String, String> = mutableMapOf<String, String>(
        "User-Agent" to "${LauncherInfo.Companion.LauncherName}/${LauncherInfo.Companion.LauncherVersion}(${LauncherInfo.Companion.LauncherCoreName}/${LauncherInfo.Companion.LauncherCoreVersion})"
    )
) {
    private val browserUserAgentBlockedList: MutableList<String> = mutableListOf(
        "edu.cn","bmclapi","mcimirror.top","mirror","aliyun","tencent","mirror.163","huaweicloud"
    )
    private val buffer: Buffer = Buffer()
    private val browserUserAgent = "Mozilla/5.0 (Win x86_64; Windows NT 10.0) Windows(Win10 22H2) Chrome/139.0.0.0 Webkit/537.5(KHTML; like Gecko) Edg/139.0.0.0"
    fun withRequestData(reqData: String): HttpRequestOptions {
        buffer.writeUtf8(reqData)
        return this
    }

    fun withRequestData(reqData: FileInputStream): HttpRequestOptions {
        reqData.use { input ->
            buffer.outputStream().use { output ->
                input.copyTo(output, 8192)
            }
        }
        return this
    }

    fun getRequest(url: String): Request {
        val body = if (method.uppercase() != "GET" && method.uppercase() != "HEAD") {
            StreamRequestBody(
                headers.get("Content-Type")?: "application/json",
                if (buffer.size > 0) buffer else null,
                buffer.size
            )
        } else null

        val builder = Request.Builder().url(url).method(method.uppercase(), body)
        headers?.forEach { (k, v) -> builder.addHeader(k, v) }
        return builder.build()
    }
    fun useBrowserUserAgent(): HttpRequestOptions{
        // 不对 BMCLAPI 和高校镜像站使用浏览器 UA
        if(url.contains("bmclapi") || url.contains("edu.cn")) return this

        else headers["User-Agent"] = browserUserAgent
        return this
    }
}

class HttpWebRequest {
    companion object {
        fun getServerResponse(options: HttpRequestOptions): Response {
            val request = options.getRequest(options.url)
            val call = Client.newCall(request)
            val response = call.execute()
            return response
        }
    }
}

fun main(){
    println(
    HttpWebRequest.getServerResponse(HttpRequestOptions("https://www.baidu.com","GET",mutableMapOf(
        "User-Agent" to "x"
    ))).body.use { body ->
        String(body?.byteStream()?.readAllBytes()?:"".toByteArray(), StandardCharsets.UTF_8)
    }
    )
}