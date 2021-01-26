package com.example.baseplugin.http

import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServer {
    /**
     * 获取资讯类型
     * type=1足球
     * type=2篮球
     * type=""查全部
     */
    @GET("http://172.21.34.101:3000/app/info")
    suspend fun getInformationType(): JsonObject

}