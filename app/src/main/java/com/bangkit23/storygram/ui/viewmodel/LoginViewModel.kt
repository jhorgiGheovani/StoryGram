package com.bangkit23.storygram.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.bangkit23.storygram.data.RepositoryMain

class LoginViewModel(private val repositoryMain: RepositoryMain) : ViewModel(){
    fun loginnewUser(email: String, password: String) = repositoryMain.loginUser(email, password)
}