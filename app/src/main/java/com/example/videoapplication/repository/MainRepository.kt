package com.example.videoapplication.repository

import com.example.videoapplication.R
import com.example.videoapplication.model.ResultsModel
import com.example.videoapplication.network.ApiService
import com.example.videoapplication.util.Resource
import com.example.videoapplication.util.Result
import com.example.videoapplication.util.awaitResult
import com.orhanobut.logger.Logger.e
import okhttp3.ResponseBody
import java.io.FileOutputStream
import java.io.InputStream


class MainRepository(
    private val apiService: ApiService,
) {
    /**
     * Making request to getUrls
     */
    suspend fun getUrls(): Resource<ResultsModel> {
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

    /**
     * Making request to get file by url, and saving it to given path
     */
    suspend fun getVideoFile(url: String, path: String): Resource<String> {
        return when (val response = apiService.downloadFileWithDynamicUrlSync(url).awaitResult()) {
            is Result.Ok -> {
                return saveFile(response.value, path)
            }
            is Result.Error -> {
                Resource.error(null, R.string.error_data_parse)
            }
            is Result.Exception -> {
                Resource.error(null, R.string.error_no_internet)
            }
        }
    }

    /**
     * BoilerPLate code to save a downloaded file
     */
    private fun saveFile(body: ResponseBody?, path: String): Resource<String> {
        if (body == null)
            return Resource.error(null, R.string.error_data_write)
        var input: InputStream? = null
        try {
            input = body.byteStream()
            //val file = File(getCacheDir(), "cacheFileAppeal.srl")
            val fos = FileOutputStream(path)
            fos.use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
        } catch (e: Exception) {
            e("saveFile", e.toString())
            return Resource.error(null, R.string.error_data_write)
        } finally {
            input?.close()
        }
        return Resource.success(path)
    }
}