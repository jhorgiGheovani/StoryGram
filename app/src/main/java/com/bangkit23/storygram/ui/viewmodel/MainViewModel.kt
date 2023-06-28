package com.bangkit23.storygram.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bangkit23.storygram.data.RepositoryMain
import com.bangkit23.storygram.data.Result
import com.bangkit23.storygram.data.remote.response.ApiResponse
import com.bangkit23.storygram.data.remote.response.ListStoryItem
import com.bangkit23.storygram.data.remote.response.Story
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainViewModel(private val repositoryMain: RepositoryMain) : ViewModel() {

    private val resultLiveData = MutableLiveData<Result<ApiResponse>>()


    val resultStoryDetail : LiveData<Story?> = repositoryMain._resultStoryDetail

    fun loadStory(token:String): LiveData<PagingData<ListStoryItem>>
        {
            return repositoryMain.loadListStory(token).cachedIn(viewModelScope)
    }

    fun addNewStory(
        token: String,
        image: MultipartBody.Part,
        desc: RequestBody,
        lat: Float?=null,
        lon: Float?=null
    ): LiveData<Result<ApiResponse>> {
        viewModelScope.launch {
            val result = repositoryMain.addNewStory(token, image, desc, lat, lon)
            resultLiveData.value = result
        }
        return resultLiveData
    }

    fun getStoryDetail(token: String, id: String) = repositoryMain.getStoryDetail(token, id)


    fun getStoryWithLocation(token:String) = repositoryMain.listStoryWithLocation(token)
}