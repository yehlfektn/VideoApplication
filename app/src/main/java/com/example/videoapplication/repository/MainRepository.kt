package com.example.videoapplication.repository

import com.example.videoapplication.R
import com.example.videoapplication.model.ResultsModel
import com.example.videoapplication.util.Resource
import com.example.videoapplication.util.Result
import com.example.videoapplication.util.awaitResult
import kz.laccent.network.ApiService


class MainRepository(
    private val apiService: ApiService,
) {
    suspend fun getUrls() : Resource<ResultsModel> {
        return when (val response = apiService.getVideoUrls().awaitResult()) {
            is Result.Ok -> {
                Resource.success(response.value)
            }
            is Result.Error -> {
                Resource.error(null, R.string.error_data_parse)
            }
            is Result.Exception -> {
                Resource.error(null, R.string.error_no_internet)
            }
        }
    }
}