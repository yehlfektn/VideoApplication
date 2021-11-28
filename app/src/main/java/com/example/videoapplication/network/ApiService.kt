package kz.laccent.network

import com.example.videoapplication.model.ResultsModel
import retrofit2.Call
import retrofit2.http.GET


interface ApiService {
    @GET("test/item")
    fun getVideoUrls(): Call<ResultsModel>
}