package com.bangkit23.storygram.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit23.storygram.R
import com.bangkit23.storygram.databinding.ActivityMainBinding
import com.bangkit23.storygram.ui.adapter.LoadingStateAdapter
import com.bangkit23.storygram.ui.adapter.StoryAdapter
import com.bangkit23.storygram.ui.viewmodel.CommonViewModel
import com.bangkit23.storygram.ui.viewmodel.MainViewModel
import com.bangkit23.storygram.ui.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity(), MenuItem.OnMenuItemClickListener {

    private lateinit var binding: ActivityMainBinding

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(application)
    }
    private val commonViewModel by viewModels<CommonViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager


        binding.swipeToRefresh.setOnRefreshListener {
            binding.swipeToRefresh.isRefreshing=false

            showStory(mainViewModel)

        }
        binding.addNewStoryFab.setOnClickListener {
            val addStoryFragment = Intent(this@MainActivity, AddNewStoryActivity::class.java)
            startActivity(addStoryFragment)
        }
        showStory(mainViewModel)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        val logout = menu.findItem(R.id.logout)
        val maps = menu.findItem(R.id.maps)


        logout.setOnMenuItemClickListener(this)
        maps.setOnMenuItemClickListener(this)

        return true
    }

    override fun onMenuItemClick(p0: MenuItem): Boolean {
        return when (p0.itemId) {
            R.id.logout -> {
                commonViewModel.setPrefernce("", this)
                val loginActivity = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(loginActivity)
                finish()
                true
            }
            R.id.maps->{
                val intent = Intent(this@MainActivity, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> false
        }
    }


    private fun showStory(mainViewModel: MainViewModel) {
        val token = getToken()
        val adapter = StoryAdapter()

        Log.d("TOKEN", token.toString())
        if (token != null) {
            binding.rvStory.adapter = adapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    adapter.retry()
                }
            )
            mainViewModel.loadStory("Bearer $token").observe(this) {
                adapter.submitData(lifecycle, it)
            }
        }
    }

    private fun getToken(): String? {
        return commonViewModel.getPreference(this).value
    }


}