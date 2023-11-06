package com.dicoding.storyapp.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.dicoding.storyapp.data.local.model.SessionModel
import com.dicoding.storyapp.utils.Result
import com.dicoding.storyapp.databinding.ActivityLoginBinding
import com.dicoding.storyapp.viewmodel.LoginViewModel
import com.dicoding.storyapp.viewmodel.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(300)
        val email = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(300)
        val edEmail = ObjectAnimator.ofFloat(binding.edLayoutEmail, View.ALPHA, 1f).setDuration(300)
        val password = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(300)
        val edPassword =
            ObjectAnimator.ofFloat(binding.edLayoutPassword, View.ALPHA, 1f).setDuration(300)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(title, email, edEmail, password, edPassword, btnLogin)
        }.start()
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val email = binding.edEmail.text
            val password = binding.edPassword.text
            loginViewModel.loginUser(email.toString(), password.toString())
                .observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }

                            is Result.Success -> {
                                loginViewModel.saveSession(
                                    SessionModel(
                                        result.data.loginResult.name,
                                        result.data.loginResult.userId,
                                        result.data.loginResult.token,
                                        true
                                    )
                                )
                                binding.progressBar.visibility = View.GONE
                                AlertDialog.Builder(this).apply {
                                    setTitle("Login ${result.data.message}!")
                                    setMessage("Welcome ${result.data.loginResult.name} to StoryApp")
                                    setPositiveButton("Next") { _, _ ->
                                        val intent = Intent(context, MainActivity::class.java)
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        startActivity(intent)
                                        finish()
                                    }
                                    create()
                                    show()
                                }
                            }

                            is Result.Error -> {
                                binding.progressBar.visibility = View.GONE
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
    }
}