package com.bangkit23.storygram.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.bangkit23.storygram.data.local.preference.SettingPreference
import com.bangkit23.storygram.data.local.room.StoryDatabase
import com.bangkit23.storygram.data.remote.requestbody.Login
import com.bangkit23.storygram.data.remote.requestbody.Register
import com.bangkit23.storygram.data.remote.response.*
import com.bangkit23.storygram.data.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.HttpException

class RepositoryMain private constructor(
    private val apiService: ApiService,
    private val storydb: StoryDatabase
) {

    val _resultStoryDetail = MutableLiveData<Story?>()

    suspend fun registerUser(
        nama: String,
        password: String,
        email: String
    ): Result<ApiResponse> {
        return try {
            val response = apiService.register(Register(nama, email, password))
            Result.Success(response)

        } catch (e: HttpException) {
            val error = e.response()?.errorBody()?.string()
            // Parse the error message from the error body
            val jsonObject = JSONObject(error!!)
            val errorMessage = jsonObject.getString("message")
            // Handle the error message
            Result.Error(errorMessage)
        } catch (e: Exception) {
            Result.Error(e.message.toString())
        }
    }

    fun loginUser(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(Login(email, password))
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun loadListStory(token: String): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storydb, apiService, token),
            pagingSourceFactory = {
                storydb.storyDAO().getAllStory()
            }
        ).liveData
    }

    fun listStoryWithLocation(token: String): LiveData<Result<List<ListStoryWIthLocation>>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.loadStroriesWithLocation(token, 100, 1)
                val list = response.listStory.map {
                    ListStoryWIthLocation(it.lon, it.id, it.lat, it.name)
                }
                emit(Result.Success(list))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }


    fun getStoryDetail(token: String, id: String): LiveData<Result<DetailStoryResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.getStroryDetail(token, id)
                _resultStoryDetail.postValue(response.story)
                emit(Result.Success(response))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }

    suspend fun addNewStory(
        token: String,
        image: MultipartBody.Part,
        desc: RequestBody,
        lat: Float? = null,
        lon: Float? = null
    ): Result<ApiResponse> {
        return try {
            val response = apiService.sendStory(token, image, desc, lat, lon)
            Result.Success(response)

        } catch (e: HttpException) {
            val error = e.response()?.errorBody()?.string()
            // Parse the error message from the error body
            val jsonObject = JSONObject(error.toString())
            val errorMessage = jsonObject.getString("message")
            // Handle the error message
            Result.Error(errorMessage)
        } catch (e: Exception) {
            Result.Error(e.message.toString())
        }
    }


    fun savePreference(token: String, context: Context) {
        val settingPreference = SettingPreference(context)
        settingPreference.setUser(token)
    }

    fun getPreference(context: Context): String? {
        val settingPreference = SettingPreference(context)
        return settingPreference.getUser()
    }

    companion object {
        @Volatile
        private var instance: RepositoryMain? = null
        fun getInstance(
            apiService: ApiService,
            storydb: StoryDatabase
        ): RepositoryMain =
            instance ?: synchronized(this) {
                instance ?: RepositoryMain(apiService, storydb)
            }.also { instance = it }
    }

}