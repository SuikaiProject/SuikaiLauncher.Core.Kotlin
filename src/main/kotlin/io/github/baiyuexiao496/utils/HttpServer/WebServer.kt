package io.github.baiyuexiao496.utils.httpServer
import com.sun.net.httpserver.HttpServer
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import okhttp3.internal.connection.Exchange
import okio.Buffer
import java.net.Inet4Address
import java.net.InetSocketAddress

class WebServer {
    private val address:String = "127.7.1.2"
    private val port:Int = 29991
    private var server: HttpServer? = null
    private fun sendResponse(exchange: HttpExchange,
                             responseCode:Int,
                             responseHanders:Map<String,String>? = null,
                             responseContent: Buffer? = null){
        exchange.sendResponseHeaders(responseCode,responseContent?.size?:-1L)
        responseContent?.copyTo(exchange.responseBody)
    }
    fun launch() {
        if (server != null) return
        server = HttpServer.create(InetSocketAddress(Inet4Address.getByAddress(address.toByteArray()), port), 0)
        server?.createContext("/")
        server?.start()
    }
    fun handleProxyRequest(exchange: HttpExchange){
        if(exchange.requestURI.path.startsWith("/api")) handleApiRequest(exchange)
        else if(exchange.requestURI.path.startsWith("/static")) handleWebRequest(exchange)
        else if(exchange.requestMethod.uppercase() == "CONNECT") handleHttpsProxy(exchange)
        else handleHttpProxy(exchange)
    }
    fun handleHttpsProxy(exchange: HttpExchange){
        // 多人游戏聊天举报绕过
        // 如果服务器强制要求签名消息，可能无法加入对应服务器
        if(exchange.requestURI.host.contains("api.minecraftservices.com") || exchange.requestURI.path.contains("player/certificates")){
            val buffer = Buffer()
            buffer.writeUtf8("<html><head><title>403 Forbidden</title><body><p>This request has been blocked.</p></body></html>")
            sendResponse(exchange,403,null,buffer)
        }
    }
    fun handleHttpProxy(exchange: HttpExchange){

    }
    fun handleApiRequest(exchange: HttpExchange){

    }
    fun handleWebRequest(exchange: HttpExchange){

    }
}