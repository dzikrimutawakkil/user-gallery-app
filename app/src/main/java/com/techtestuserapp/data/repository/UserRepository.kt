package com.techtestuserapp.data.repository

import com.techtestuserapp.data.local.dao.UserDao
import com.techtestuserapp.data.local.entity.UserEntity
import com.techtestuserapp.data.remote.ApiService
import com.techtestuserapp.data.remote.response.PostResponse
import com.techtestuserapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val userDao: UserDao
) {
    fun getUsers(forceRefresh: Boolean): Flow<Resource<List<UserEntity>>> = flow {
        emit(Resource.Loading())

        val cachedUsers = userDao.getAllUsers()

        if (forceRefresh || cachedUsers.first().isEmpty()) {
            try {
                val remoteUsers = apiService.getUsers()
                userDao.clearUsers()
                userDao.insertUsers(remoteUsers.map { it.toUserEntity() })
            } catch (e: HttpException) {
                emit(Resource.Error(
                    message = "Gagal mengambil data: ${e.message()}",
                    data = cachedUsers.first()
                ))
            } catch (e: IOException) {
                emit(Resource.Error(
                    message = "Tidak ada koneksi internet. Menampilkan data offline.",
                    data = cachedUsers.first()
                ))
            }
        }
        val newCache = userDao.getAllUsers().first()
        emit(Resource.Success(newCache))
    }

    fun getPosts(userId: Int): Flow<Resource<List<PostResponse>>> = flow {
        emit(Resource.Loading())
        try {
            val posts = apiService.getPosts(userId)
            emit(Resource.Success(posts))
        } catch (e: HttpException) {
            emit(Resource.Error("Gagal mengambil data: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Tidak ada koneksi internet."))
        }
    }
}