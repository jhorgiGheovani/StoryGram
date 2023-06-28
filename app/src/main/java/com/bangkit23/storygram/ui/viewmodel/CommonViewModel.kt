package com.bangkit23.storygram.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit23.storygram.data.RepositoryMain

class CommonViewModel(private val repositoryMain: RepositoryMain) : ViewModel() {

    private val token = MutableLiveData<String?>()

    fun setPrefernce(token: String, context: Context) =
        repositoryMain.savePreference(token, context)

    fun getPreference(context: Context): LiveData<String?> {
        val tokenData = repositoryMain.getPreference(context)
        token.value = tokenData
        return token
    }
}