package vn.namnt.nabweather.data.internal.remote.interceptors

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import okhttp3.Interceptor
import okhttp3.Response
import vn.namnt.nabweather.data.NoConnectivityException
import vn.namnt.nabweather.data.NoInternetException
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject

/**
 * @author namnt
 * @since 05/12/2022
 */
class NoConnectionInterceptor @Inject constructor(
    private val context: Context
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return if (!isConnectionOn()) {
            throw NoConnectivityException()
        } else if (!isInternetAvailable()) {
            throw NoInternetException()
        } else {
            chain.proceed(chain.request())
        }
    }

    private fun isConnectionOn(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as
                    ConnectivityManager
        val network = connectivityManager.activeNetwork
        val connection =
            connectivityManager.getNetworkCapabilities(network)

        return connection != null && (
                connection.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        connection.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    private fun isInternetAvailable(): Boolean {
        return try {
            val timeoutMs = 1500
            val sock = Socket()
            val socketAddress = InetSocketAddress("8.8.8.8", 53)

            sock.connect(socketAddress, timeoutMs)
            sock.close()

            true
        } catch (e: IOException) {
            false
        }
    }
}
