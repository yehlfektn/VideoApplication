package com.example.videoapplication.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.videoapplication.repository.MainRepository
import com.example.videoapplication.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

/**
 * ViewModel for each fragment, used to check if video file exists, if not retrieve it
 */
class PageViewModel(
    private val mainRepository: MainRepository,
    app: Application
) : AndroidViewModel(app) {
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _filePath = MutableLiveData<Resource<String>>()
    val filePath: LiveData<Resource<String>> = _filePath

    /***
     * Check if file is already downloaded, if yes, return path. If no, download the file.
     */
    fun getVideo(url: String) {
        scope.launch {
            _filePath.value = Resource.loading(null)

            val fileName = url.substring(url.lastIndexOf("/") + 1)
            val path = getApplication<Application>().filesDir.absolutePath + fileName
            val file = File(path)

            if (file.exists()) {
                _filePath.value = Resource.success(path)
            } else {
                _filePath.value = mainRepository.getVideoFile(url, path)
            }
        }
    }
}