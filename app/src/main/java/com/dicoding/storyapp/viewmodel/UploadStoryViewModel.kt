package com.dicoding.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.repository.StoryRepository
import java.io.File

class UploadStoryViewModel(private val repository: StoryRepository) : ViewModel() {
    fun uploadImage(file: File, description: String) = repository.uploadImage(file, description)

    fun uploadImageWithLocation(
        imageFile: File,
        description: String,
        latitude: Double,
        longitude: Double
    ) = repository.uploadImageWithLocation(imageFile, description, latitude, longitude)
}