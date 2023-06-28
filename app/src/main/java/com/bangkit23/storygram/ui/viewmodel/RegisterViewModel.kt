package com.bangkit23.storygram.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit23.storygram.data.RepositoryMain
import com.bangkit23.storygram.data.Result
import com.bangkit23.storygram.data.remote.response.ApiResponse
import kotlinx.coroutines.launch

class RegisterViewModel(private val repositoryMain: RepositoryMain) : ViewModel() {

    private val resultLiveData = MutableLiveData<Result<ApiResponse>>()

    fun registerNewUser(
        nama: String,
        password: String,
        email: String
    ): LiveData<Result<ApiResponse>> {
        viewModelScope.launch {
            val result = repositoryMain.registerUser(nama, password, email)
            resultLiveData.value = result
        }
        return resultLiveData
    }
}