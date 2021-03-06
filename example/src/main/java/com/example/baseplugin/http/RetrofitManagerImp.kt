package com.example.baseplugin.http


import com.harrison.plugin.http.HttpsUtils
import com.harrison.plugin.http.convert.GsonConverterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.lang.Exception
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext


object  RetrofitManagerImp {

    const val DEFAULT_HOST = "https://xj-sb-asia-yb5.2r9qgy.com"
    private var apiService: ApiServer? = null

    /**
     * OKHttp 配置
     */
    private fun configOkHttp(build: OkHttpClient.Builder) {
        build.connectTimeout(20, TimeUnit.SECONDS)
            var sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, arrayOf(HttpsUtils.UnSafeTrustManager), SecureRandom())

            build.sslSocketFactory(sslContext.getSocketFactory(),HttpsUtils.UnSafeTrustManager)
            build.hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier)
    }

    /**
     * Retrofit 配置
     */
    private fun configRetrofit(builder: Retrofit.Builder) {
        builder.baseUrl(DEFAULT_HOST)
            .validateEagerly(true)
            .addConverterFactory(GsonConverterFactory.create())
    }

    /**
     * 获取ApiInterface
     */
    fun instance(): ApiServer {
        if (apiService == null) {
            //OKHttp
            val okHttpBuilder = OkHttpClient.Builder()
            configOkHttp(okHttpBuilder)


            //retrofit
            var builder = Retrofit.Builder()
            configRetrofit(builder)
            builder.client(okHttpBuilder.build())

            apiService = builder.build().create(ApiServer::class.java)
        }
        return apiService!!
    }


}

