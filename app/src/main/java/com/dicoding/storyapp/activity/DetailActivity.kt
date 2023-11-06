package com.dicoding.storyapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.dicoding.storyapp.databinding.ActivityDetailBinding
import com.dicoding.storyapp.utils.Result
import com.dicoding.storyapp.viewmodel.DetailViewModel
import com.dicoding.storyapp.viewmodel.ViewModelFactory

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(application)
    }

    companion object {
        const val USER_ID = "user_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
    }

    private fun setupAction() {
        val userId = intent.getStringExtra(USER_ID)

        detailViewModel.getDetailStory(userId!!).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    // Do Nothing
                }

                is Result.Success -> {
                    binding.tvName.text = result.data.story.name
                    binding.tvDescription.text = result.data.story.description
                    Glide.with(this)
                        .load(result.data.story.photoUrl)
                        .into(binding.ivItem)
                }

                is Result.Error -> {
                    Toast.makeText(
                        application,
                        "Error: ${result.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}