package com.example.videoapplication.network

import com.example.videoapplication.model.ResultsModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url


interface ApiService {
    @GET("test/item")
    fun getVideoUrls(): Call<ResultsModel>

    @GET
    fun downloadFileWithDynamicUrlSync(@Url fileUrl: String): Call<ResponseBody>
}