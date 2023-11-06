package com.dicoding.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.storyapp.data.local.model.SessionModel
import com.dicoding.storyapp.data.local.preferences.SessionPreferences
import com.dicoding.storyapp.data.remote.response.DetailStoryResponse
import com.dicoding.storyapp.data.remote.response.ErrorResponse
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import com.dicoding.storyapp.data.remote.retrofit.ApiService
import com.dicoding.storyapp.paging.StoryPagingSource
import com.dicoding.storyapp.utils.Result
import com.google.gson.Gson
import retrofit2.HttpException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository
private constructor(
    private val apiService: ApiService,
    private val sessionPreferences: SessionPreferences
) {
    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(
            apiService: ApiService, sessionPreferences: SessionPreferences
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, sessionPreferences)
            }.also { instance = it }
    }

    fun registerUser(name: String, email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.registerUser(name, email, password).message
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage!!))
        }
    }

    fun loginUser(email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.loginUser(email, password)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage!!))
        }
    }

    fun getSession(): Flow<SessionModel> {
        return sessionPreferences.getSession()
    }

    suspend fun saveSession(sessionModel: SessionModel) {
        sessionPreferences.saveSession(sessionModel)
    }

    suspend fun logout() {
        sessionPreferences.logout()
    }

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        val user = runBlocking { sessionPreferences.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return Pager(
            config = PagingConfig(pageSize = 5),
            pagingSourceFactory = {
                StoryPagingSource(apiService)
            }
        ).liveData
    }

    fun getDetailStory(id: String): LiveData<Result<DetailStoryResponse>> = liveData {
        try {
            val user = runBlocking { sessionPreferences.getSession().first() }
            val apiService = ApiConfig.getApiService(user.token)
            val response = apiService.getDetailStory(id)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun uploadImage(imageFile: File, description: String) = liveData {
        emit(Result.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val user = runBlocking { sessionPreferences.getSession().first() }
            val apiService = ApiConfig.getApiService(user.token)
            val successResponse = apiService.uploadImage(multipartBody, requestBody)
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Result.Error(errorResponse.message.toString()))
        }
    }

    fun uploadImageWithLocation(
        imageFile: File,
        description: String,
        latitude: Double,
        longitude: Double
    ) = liveData {
        emit(Result.Loading)
        val descriptionRequestBody = description.toRequestBody("text/plain".toMediaType())
        val latitudeRequestBody = latitude.toString().toRequestBody(MultipartBody.FORM)
        val longitudeRequestBody = longitude.toString().toRequestBody(MultipartBody.FORM)
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val user = runBlocking { sessionPreferences.getSession().first() }
            val apiService = ApiConfig.getApiService(user.token)
            val successResponse =
                apiService.uploadImageWithLocation(multipartBody, descriptionRequestBody, latitudeRequestBody, longitudeRequestBody)
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(Result.Error(errorResponse.message.toString()))
        }
    }

    fun getStoriesWithLocation() = liveData {
        emit(Result.Loading)
        try {
            val user = runBlocking { sessionPreferences.getSession().first() }
            val apiService = ApiConfig.getApiService(user.token)
            val mapsResponse = apiService.getStoriesWithLocation()
            emit(Result.Success(mapsResponse.listStory))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage!!))
        }
    }
}