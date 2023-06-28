package com.bangkit23.storygram.data.remote.retrofit


import com.bangkit23.storygram.data.remote.requestbody.Login
import com.bangkit23.storygram.data.remote.requestbody.Register
import com.bangkit23.storygram.data.remote.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @POST("register")
    suspend fun register(
        @Body requestBody: Register
    ): ApiResponse

    @POST("login")
    suspend fun login(
        @Body requestBody: Login
    ): LoginResponse


    @Multipart
    @POST("stories")
    suspend fun sendStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") langitude: Float? = null,
        @Part("lon") longitude: Float? = null,
    ): ApiResponse

    @GET("stories")
    suspend fun loadStrories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int): ListStoryResponse

    @GET("stories/{id}")
    suspend fun getStroryDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): DetailStoryResponse

    @GET("stories")
    suspend fun loadStroriesWithLocation(
        @Header("Authorization") token: String,
        @Query("size") size: Int,
        @Query("location") location: Int): ListStoryResponse

}