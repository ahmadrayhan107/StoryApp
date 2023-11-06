package com.dicoding.storyapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.adapter.LoadingStateAdapter
import com.dicoding.storyapp.adapter.StoryAdapter
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.viewmodel.MainViewModel
import com.dicoding.storyapp.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        showRecyclerList()
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.getSession()
    }

    private fun setupAction() {
        mainViewModel.getSession().observe(this) { session ->
            if (!session.isLogin) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                binding.progressBar.visibility = View.GONE
                getStoriesData()
            }
        }

        binding.toAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuLogout -> {
                    AlertDialog.Builder(this).apply {
                        setTitle("Message")
                        setMessage("Are you sure to Logout?")
                        setPositiveButton("Yes") { _, _ ->
                            mainViewModel.logout()
                        }
                        setNegativeButton("No") { _, _ ->
                            // Do Nothing
                        }
                        create()
                        show()
                    }

                    true
                }

                R.id.menuMaps -> {
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)

                    true
                }

                else -> false
            }
        }

        binding.fabCamera.setOnClickListener {
            val intent = Intent(this, UploadStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showRecyclerList() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStories.addItemDecoration(itemDecoration)
    }

    private fun getStoriesData() {
        val adapter = StoryAdapter()
        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        mainViewModel.getStories().observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }
}