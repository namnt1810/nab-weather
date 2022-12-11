package vn.namnt.nabweather.data.remote.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * @author namnt
 * @since 05/12/2022
 */
class RequestInterceptor @Inject constructor(
    private val appId: String
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val originalUrl = original.url

        val requestUrl = originalUrl.newBuilder()
            .addQueryParameter("appid", appId)
            .build()

        val request = original.newBuilder()
            .url(requestUrl)
            .build()

        return chain.proceed(request)
    }
}
