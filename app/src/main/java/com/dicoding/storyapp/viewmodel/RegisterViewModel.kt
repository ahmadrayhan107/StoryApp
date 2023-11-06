package com.dicoding.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.repository.StoryRepository

class RegisterViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun registerUser(name: String, email: String, password: String) = storyRepository.registerUser(name, email, password)
}