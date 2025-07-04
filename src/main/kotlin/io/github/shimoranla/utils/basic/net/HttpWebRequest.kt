package io.github.shimoranla.utils.basic.net

import io.github.shimoranla.utils.basic.LauncherInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okio.BufferedSink
import okio.Buffer
import java.io.FileInputStream
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

val Client: OkHttpClient = OkHttpClient.Builder()
    .connectionPool(ConnectionPool(1024, 5L, TimeUnit.MINUTES))
    .build()

class HttpProxy {
    // 代理逻辑占位
}

class StreamRequestBody(
    private val mediaType: String = "application/json",
    private val requestStream: Buffer?,
    private val contentLength: Long
) : RequestBody() {
    override fun contentLength(): Long = contentLength
    override fun contentType(): MediaType? = mediaType.toMediaType()

    override fun writeTo(sink: BufferedSink) {
        requestStream?.let { sink.writeAll(it.clone()) } ?: sink.writeUtf8("")
    }
}

data class HttpRequestOptions(
    val url: String,
    val method: String,
    var headers: MutableMap<String, String>? = null,
    val retryCount: Int = 3,
    val retryDelayMillis: Long = 200,
    val ensureSuccessStatus:Boolean = true
) {
    private val buffer: Buffer = Buffer()

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
                headers?.get("Content-Type") ?: "application/json",
                if (buffer.size > 0) buffer else null,
                buffer.size
            )
        } else null

        val builder = Request.Builder()
            .url(url)
            .method(method.uppercase(), body)
        if(headers == null) headers = mutableMapOf()
        if(headers != null && !headers!!.contains("User-Agent")) headers!!["User-Agent"] = "${LauncherInfo.LauncherName}/${LauncherInfo.LauncherVersion}(${LauncherInfo.LauncherCoreName}/${LauncherInfo.LauncherCoreVersion})"
        headers?.forEach { (k, v) -> builder.addHeader(k, v) }

        return builder.build()
    }
}

class HttpWebRequest {
    companion object {
        // 异步获取服务器响应（带重试机制）
        suspend fun getServerResponse(options: HttpRequestOptions): Response =
            withContext(Dispatchers.IO) {
                var lastException: Throwable? = null
                repeat(options.retryCount) { retry ->
                    try {
                        val request = options.getRequest(options.url)
                        val call = Client.newCall(request)
                        return@withContext call.await()
                    } catch (ex: Exception) {
                        lastException = ex
                        if (retry < options.retryCount - 1) {
                            delay(options.retryDelayMillis)
                        }
                    }
                }
                throw RuntimeException("发送网络请求时出现错误", lastException)
            }

        // OkHttp 的协程扩展
        suspend fun Call.await(options:HttpRequestOptions? = null): Response {
            return suspendCancellableCoroutine { continuation ->
                enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        continuation.resume(response)
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        if (continuation.isCancelled) return
                        if(options != null && options.ensureSuccessStatus) continuation.resumeWithException(e)
                    }
                })

                // 处理协程取消
                continuation.invokeOnCancellation {
                    try {
                        cancel()
                    } catch (ex: Throwable) {

                    }
                }
            }
        }
    }
}