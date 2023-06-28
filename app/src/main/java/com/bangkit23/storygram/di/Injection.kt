package com.bangkit23.storygram.di

import android.content.Context
import com.bangkit23.storygram.data.RepositoryMain
import com.bangkit23.storygram.data.local.room.StoryDatabase
import com.bangkit23.storygram.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): RepositoryMain {
        val apiService = ApiConfig.getApiService()
        val database= StoryDatabase.getInstance(context)
        return RepositoryMain.getInstance(apiService,database)
    }
}