package com.wasim.socketdemo.network

import android.app.Application
import android.util.Log
import android.widget.Toast
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import java.net.URISyntaxException
import com.wasim.socketdemo.R


class SocketManager : Application() {

    private var mSocket: Socket? = null

    init {
        mSocket = try {
            IO.socket("https://k11api.fansedge.in/")
        } catch (e: URISyntaxException) {
            Toast.makeText(this, getString(R.string.connection_error), Toast.LENGTH_SHORT).show()
            //throw RuntimeException()
            null
        }
    }

    fun getSocket(): Socket? {
        return mSocket
    }

    fun getEmitterListener(event: String, listener: Emitter.Listener): Emitter? {
        return getSocket()!!.on(event, listener)
    }

    fun connect() {
        getSocket()!!.connect()
    }

    fun disconnect() {
        getSocket()!!.disconnect()
    }

    fun sendOnSocket(event: String, message: String) {
        getSocket()!!.emit(event, message)
    }

    init {
        getSocket()!!.connect()
    }
}