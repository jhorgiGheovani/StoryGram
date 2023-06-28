package com.bangkit23.storygram.ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bangkit23.storygram.R
import com.bangkit23.storygram.data.Result
import com.bangkit23.storygram.data.remote.response.Story
import com.bangkit23.storygram.databinding.ActivityDetailsBinding
import com.bangkit23.storygram.ui.dataModel.MapsDetailsModel
import com.bangkit23.storygram.ui.viewmodel.CommonViewModel
import com.bangkit23.storygram.ui.viewmodel.MainViewModel
import com.bangkit23.storygram.ui.viewmodel.ViewModelFactory
import com.bangkit23.storygram.utils.convertDate
import com.bumptech.glide.Glide

class DetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsBinding

    private var idVal: String= ""

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(application)
    }
    private val commonViewModel by viewModels<CommonViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= 33) {
            val id = intent.getStringExtra("id")

            if (id != null) {
                idVal=id


                if (savedInstanceState === null){
                    setContent(mainViewModel, id)
                }
                mainViewModel.resultStoryDetail.observe(this){
                    if (it != null) {
                        bindingData(it)
                    }
                    binding.progressBar.visibility = View.GONE
                }
            }

        } else {
            @Suppress("DEPRECATION")
            val id = intent.getStringExtra("id") as String
            idVal=id

            if (savedInstanceState === null){
                setContent(mainViewModel, id)
            }
            mainViewModel.resultStoryDetail.observe(this){
                if (it != null) {
                    bindingData(it)
                }
                Log.d("resultStoryDetail", "resultStoryDetail")
                binding.progressBar.visibility = View.GONE
            }

        }

    }

    private fun setContent(mainViewModel: MainViewModel, id: String) {
        val token = getToken()
        if (token != null) {
            mainViewModel.getStoryDetail("Bearer $token", id)
                .observe(this@DetailsActivity) { detail ->
                    if (detail != null) {
                        when (detail) {
                            is Result.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                                binding.descTv.text=getString(R.string.loading_placeholder)
                                binding.nameTv.text=getString(R.string.loading_placeholder)
                                binding.dateTv.text=getString(R.string.loading_placeholder)
                            }
                            is Result.Success -> {
                                binding.progressBar.visibility = View.GONE
                                val data = detail.data.story
                                if (data != null) {
//                                    bindingData(data)
                                    setButtonEnable(data.lat,data.lon)
                                    if(data.lon!=null&&data.lat!=null){
                                        sendLocationDataToMapActivity(data.id.toString(),data.name.toString(),data.lat,data.lon)
                                    }

                                }
                            }
                            is Result.Error -> {
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(this, detail.error, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
        }
    }

    private fun getToken(): String? {
        return commonViewModel.getPreference(this).value
    }

    private fun bindingData(data: Story) {
        binding.nameTv.text = data.name
        binding.descTv.text = data.description
        if (data.createdAt != null) {
            binding.dateTv.text = convertDate(data.createdAt)
        }
        Glide.with(this)
            .load(data.photoUrl)
            .into(binding.photoIV)
    }

    private fun setButtonEnable(lat: Float?=null, lon: Float?=null){
        binding.buttonToMaps.isEnabled = lat!=null && lon!=null

        binding.buttonToMaps.setOnClickListener {
            val intent = Intent(this@DetailsActivity,MapsDetailsStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun sendLocationDataToMapActivity(id: String, name:String,lon:Float, lat:Float){
        val locationData= MapsDetailsModel(id,name,lon,lat)
        binding.buttonToMaps.setOnClickListener{
            val intent = Intent(this@DetailsActivity,MapsDetailsStoryActivity::class.java)
            intent.putExtra(EXTRA_LOCATION_DATA,locationData)
            startActivity(intent)
        }
    }

    companion object{
        const val EXTRA_LOCATION_DATA = "locationData"
    }
}