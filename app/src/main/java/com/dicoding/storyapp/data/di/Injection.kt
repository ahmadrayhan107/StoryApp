package com.dicoding.storyapp.data.di

import android.content.Context
import com.dicoding.storyapp.data.local.preferences.SessionPreferences
import com.dicoding.storyapp.data.local.preferences.datastore
import com.dicoding.storyapp.data.remote.retrofit.ApiConfig
import com.dicoding.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val sessionPreferences = SessionPreferences.getInstance(context.datastore)
        val user = runBlocking { sessionPreferences.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return StoryRepository.getInstance(apiService, sessionPreferences)
    }
}