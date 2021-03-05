package com.example.baseplugin.http


import com.harrison.plugin.http.convert.GsonConverterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit


object  RetrofitManagerImp {

    const val DEFAULT_HOST = "http://172.21.34.101:3000"
    private var apiService: ApiServer? = null

    /**
     * OKHttp 配置
     */
    private fun configOkHttp(build: OkHttpClient.Builder) {
        build.connectTimeout(20, TimeUnit.SECONDS)
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

