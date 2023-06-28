package com.bangkit23.storygram.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit23.storygram.data.RepositoryMain
import com.bangkit23.storygram.di.Injection

class ViewModelFactory private constructor(private val repositoryMain: RepositoryMain) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repositoryMain) as T
        }else if(modelClass.isAssignableFrom(LoginViewModel::class.java)){
            return LoginViewModel(repositoryMain) as T
        }else if(modelClass.isAssignableFrom(CommonViewModel::class.java)){
            return CommonViewModel(repositoryMain) as T
        }else if(modelClass.isAssignableFrom(RegisterViewModel::class.java)){
            return RegisterViewModel(repositoryMain) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}