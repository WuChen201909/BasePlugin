package com.example.baseplugin.http

import com.google.gson.JsonObject
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiServer {
    /**
     * 获取资讯类型
     * type=1足球
     * type=2篮球
     * type=""查全部
     */
    @POST("/zh-cn/serv/getmenu")
    @Headers(
        *arrayOf(
            "Referer: https://site5.hnzae.com"
        )
    )
    suspend fun getInformationType(@Header("Cookie") cookie: String, @Body route: RequestBody): JsonObject


}