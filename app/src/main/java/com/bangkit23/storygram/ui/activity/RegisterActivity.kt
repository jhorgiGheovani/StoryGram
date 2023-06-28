package com.bangkit23.storygram.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bangkit23.storygram.data.Result
import com.bangkit23.storygram.data.remote.response.AutoLoginModel
import com.bangkit23.storygram.databinding.ActivityRegisterBinding
import com.bangkit23.storygram.ui.viewmodel.RegisterViewModel
import com.bangkit23.storygram.ui.viewmodel.ViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val registerViewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Get value from edit text
        val nameVal = binding.edRegisterName.text
        val emailVal = binding.edRegisterEmail.text
        val passwordVal = binding.edRegisterPassword.text



        binding.btnRegister.setOnClickListener {
            if (binding.edRegisterPassword.error == null&&binding.edRegisterEmail.error==null) {
                loadingProcess()
                registerViewModel.registerNewUser(
                    nameVal.toString(),
                    passwordVal.toString(),
                    emailVal.toString()
                ).observe(this) { result ->
                    when (result) {
                        is Result.Loading -> {}
                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val response = result.data
                            Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                            sendDataToLoginActivity(
                                AutoLoginModel(
                                    emailVal.toString(),
                                    passwordVal.toString()
                                )
                            )
                            finish()
                        }
                        is Result.Error -> {
                            val errorMessage = result.error
                            wrongDataGiven()
                            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }


    private fun loadingProcess() {
        binding.progressBar.visibility = View.VISIBLE
        binding.edRegisterName.isCursorVisible = false
        binding.edRegisterEmail.isCursorVisible = false
        binding.edRegisterPassword.isCursorVisible = false
    }

    private fun wrongDataGiven() {
        binding.progressBar.visibility = View.GONE
        binding.edRegisterName.isCursorVisible = true
        binding.edRegisterEmail.isCursorVisible = true
        binding.edRegisterPassword.isCursorVisible = true
    }

    private fun sendDataToLoginActivity(data: AutoLoginModel) {
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        intent.putExtra("extra_email_username", data)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }


}