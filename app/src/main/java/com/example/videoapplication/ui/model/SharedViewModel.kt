package com.example.videoapplication.ui.model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.videoapplication.model.ResultsModel
import com.example.videoapplication.repository.MainRepository
import com.example.videoapplication.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Main responsibility of this viewModel to retrieve urls, and share to fragments.
 */
class SharedViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {
    private val scope = CoroutineScope(Dispatchers.Main)


    private val _results = MutableLiveData<Resource<ResultsModel>>()
    val resultsModel: LiveData<Resource<ResultsModel>> = _results


    fun getUrls() {
        scope.launch {
            _results.value = Resource.loading(null)
            _results.value = mainRepository.getUrls()
        }
    }
}