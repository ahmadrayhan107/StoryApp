package com.dicoding.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.local.model.SessionModel
import com.dicoding.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val storyRepository: StoryRepository)  : ViewModel() {
    fun loginUser(email: String, password: String) = storyRepository.loginUser(email, password)

    fun saveSession(sessionModel: SessionModel) {
        viewModelScope.launch {
            storyRepository.saveSession(sessionModel)
        }
    }
}