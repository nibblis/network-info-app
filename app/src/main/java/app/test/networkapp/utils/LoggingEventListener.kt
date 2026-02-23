package app.test.networkapp.utils

import android.util.Log
import okhttp3.Call
import okhttp3.EventListener
import okhttp3.Handshake
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit

/**
 * An EventListener that logs the major steps of the HTTP request lifecycle.
 * This helps in analyzing network performance and understanding the impact of
 * DNS resolution, TCP connection, and TLS handshake.
 */
class LoggingEventListener : EventListener() {
    private var callStartNanos: Long = 0

    private fun printEvent(name: String) {
        val elapsedNanos = System.nanoTime() - callStartNanos
        val elapsedMillis = TimeUnit.NANOSECONDS.toMillis(elapsedNanos)
        Log.d("NetworkLifecycle", "$name in ${elapsedMillis}ms")
    }

    override fun callStart(call: Call) {
        callStartNanos = System.nanoTime()
        Log.d("NetworkLifecycle", "callStart: ${call.request().url}")
    }

    override fun dnsStart(call: Call, domainName: String) {
        printEvent("dnsStart")
    }

    override fun dnsEnd(call: Call, domainName: String, inetAddressList: List<InetAddress>) {
        printEvent("dnsEnd")
    }

    override fun connectStart(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy) {
        printEvent("connectStart (TCP)")
    }

    override fun secureConnectStart(call: Call) {
        printEvent("secureConnectStart (TLS Handshake)")
    }

    override fun secureConnectEnd(call: Call, handshake: Handshake?) {
        printEvent("secureConnectEnd (TLS Handshake)")
    }

    override fun connectEnd(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy, protocol: Protocol?) {
        printEvent("connectEnd (TCP)")
    }

    override fun connectFailed(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy, protocol: Protocol?, ioe: IOException) {
        printEvent("connectFailed")
    }

    override fun requestHeadersStart(call: Call) {
        printEvent("requestHeadersStart")
    }

    override fun requestHeadersEnd(call: Call, request: Request) {
        printEvent("requestHeadersEnd")
    }

    override fun responseHeadersStart(call: Call) {
        printEvent("responseHeadersStart")
    }

    override fun responseHeadersEnd(call: Call, response: Response) {
        printEvent("responseHeadersEnd")
    }

    override fun callEnd(call: Call) {
        printEvent("callEnd")
    }

    override fun callFailed(call: Call, ioe: IOException) {
        printEvent("callFailed")
    }
}