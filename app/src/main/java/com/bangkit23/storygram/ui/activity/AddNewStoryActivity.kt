package com.bangkit23.storygram.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bangkit23.storygram.R
import com.bangkit23.storygram.data.Result
import com.bangkit23.storygram.databinding.ActivityAddNewStoryBinding
import com.bangkit23.storygram.ui.viewmodel.CommonViewModel
import com.bangkit23.storygram.ui.viewmodel.MainViewModel
import com.bangkit23.storygram.ui.viewmodel.ViewModelFactory
import com.bangkit23.storygram.utils.reduceFileImage
import com.bangkit23.storygram.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddNewStoryActivity : AppCompatActivity(), MenuItem.OnMenuItemClickListener{

    private lateinit var binding: ActivityAddNewStoryBinding
    private var getFile: File? = null
    private var latitude: Double? = null
    private var longitude: Double? = null

    //LOCATION
    private lateinit var locationManager: LocationManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(application)
    }
    private val commonViewModel by viewModels<CommonViewModel> {
        ViewModelFactory.getInstance(application)
    }

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Permission Denied",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }

    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddNewStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        //Location
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // initialize fused location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        //checkbox
        val checkBox = binding.includeLocationCheckBox
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getCurrentLocation()
            } else {
                longitude = null
                latitude = null
            }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_add_new_story, menu)

        val openCamera = menu?.findItem(R.id.open_camera)
        val openGallery = menu?.findItem(R.id.open_gallery)
        val sendStory = menu?.findItem(R.id.send)

        openCamera?.setOnMenuItemClickListener(this)
        openGallery?.setOnMenuItemClickListener(this)
        sendStory?.setOnMenuItemClickListener(this)

        return true
    }

    override fun onMenuItemClick(p0: MenuItem): Boolean {
        return when (p0.itemId) {
            R.id.open_camera -> {
                startCameraX()
                true
            }
            R.id.open_gallery -> {
                startGallery()
                true
            }
            R.id.send -> {
                val token = commonViewModel.getPreference(this).value
                val edDesc = binding.edDeskripsi
                if (token != null) {
                    uploadStory(edDesc, token)
                }

                true
            }
            else -> false
        }
    }


    //Open CameraX
    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra(CameraActivity.EXTRA_PICTURE, File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra(CameraActivity.EXTRA_PICTURE)
            } as? File

//            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let { file ->
//                rotateFile(file, isBackCamera)
                getFile = file
                binding.preview.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    //Open Gallery
    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@AddNewStoryActivity)
                getFile = myFile
                binding.preview.setImageURI(uri)
            }
        }
    }


    //Send Story ke Server API
    private fun uploadStory(description: EditText, token: String) {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val desc = description.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            mainViewModel.addNewStory("Bearer $token", imageMultipart, desc, latitude?.toFloat(), longitude?.toFloat())
                .observe(this) { result ->
                    when (result) {
                        is Result.Loading -> {
                            Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show()
                        }
                        is Result.Success -> {
                            val response = result.data
                            val intent = Intent(this@AddNewStoryActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                            Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        is Result.Error -> {
                            val errorMessage = result.error
                            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }


        } else {
            Toast.makeText(
                this@AddNewStoryActivity,
                "Silakan masukkan berkas gambar terlebih dahulu.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }



    //LOCATION
    private fun getCurrentLocation() {
        // checking location permission
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // request permission
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_PERMISSIONS)

            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                // getting the last known or current location
                latitude = location.latitude
                longitude = location.longitude
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed on getting current location",
                    Toast.LENGTH_SHORT).show()
            }
    }




}