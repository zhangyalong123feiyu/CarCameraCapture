package com.lenovo.carcontroler.utils

import android.content.Context
import com.lenovo.carcamear1capture.WsListener
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit


class WebSocketUtil {
    private lateinit var wsListener: WsListener
    private lateinit var mWebSocket: WebSocket
    private lateinit var okHttpClient: OkHttpClient
    private val mWbSocketUrl = "ws://10.110.129.178:7861/input"
    fun init(/*url:String,*/context:Context):WebSocket {
        okHttpClient = OkHttpClient.Builder()
            .pingInterval(10, TimeUnit.SECONDS)
            .build()
        val request: Request = Request.Builder()
            .url(mWbSocketUrl)
            .build()
        wsListener=WsListener(context);
        mWebSocket = okHttpClient.newWebSocket(request, wsListener)
        return mWebSocket
    }

    fun getWebSocketClient():WebSocket{
        return mWebSocket;
    }

    fun getWebSocketListener():WsListener{
        return wsListener;
    }
}