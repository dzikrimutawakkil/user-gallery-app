package com.techtestuserapp.data.remote

import com.techtestuserapp.data.remote.response.PostResponse
import com.techtestuserapp.data.remote.response.UserResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("users")
    suspend fun getUsers(): List<UserResponse>

    @GET("posts")
    suspend fun getPosts(@Query("userId") userId: Int): List<PostResponse>
}