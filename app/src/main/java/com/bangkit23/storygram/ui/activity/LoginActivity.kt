package com.bangkit23.storygram.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bangkit23.storygram.data.Result
import com.bangkit23.storygram.data.remote.response.AutoLoginModel
import com.bangkit23.storygram.databinding.ActivityLoginBinding
import com.bangkit23.storygram.ui.viewmodel.CommonViewModel
import com.bangkit23.storygram.ui.viewmodel.LoginViewModel
import com.bangkit23.storygram.ui.viewmodel.ViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(application)
    }

    private val commonViewModel by viewModels<CommonViewModel> {
        ViewModelFactory.getInstance(application)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Login"
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnRegister = binding.btnRegister
        val btnLogin = binding.btnLogin
        val edEmail = binding.edLoginEmail
        val edPassword = binding.edLoginPassword

        //Cek sesi login
        isLoginBefore(this)

        //Masuk ke halaman register
        btnRegister.setOnClickListener {
            val register = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(register)
        }

        getDataFromRegister()


        btnLogin.setOnClickListener {
            //Check If password kosong
            if (edPassword.text?.isEmpty() == true) {
                edPassword.error = "Password tidak boleh kosong"
            }

            if (edEmail.text?.isEmpty() == true) {
                edEmail.error = "Email tidak boleh kosong"
            }

            if (edPassword.error == null && edEmail.error == null) {
                userLogin(edEmail.text.toString(), edPassword.text.toString())
            }


        }
    }


    private fun userLogin(email: String, password: String) {
        loginViewModel.loginnewUser(email, password).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        loadingProcess()
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val data = result.data
                        Toast.makeText(this@LoginActivity, data.message, Toast.LENGTH_SHORT).show()
                        if (data.loginResult?.token != null) {
                            commonViewModel.setPrefernce(data.loginResult.token, this)
                        }
                        val mainActivity = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(mainActivity)
                        finish()//end this activity after login
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }

    private fun isLoginBefore(context: Context) {
        //Cek Sesi Login
        commonViewModel.getPreference(context).observe(this) { token ->
            if (token?.isEmpty() == false) {
                val mainActivity = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(mainActivity)
                finish()
            }
        }
    }

    private fun loadingProcess() {
        binding.progressBar.visibility = View.VISIBLE
        binding.edLoginEmail.isCursorVisible = false
        binding.edLoginPassword.isCursorVisible = false
    }

    private fun getDataFromRegister(){
        if (Build.VERSION.SDK_INT >= 33) {
            val data = intent.getParcelableExtra("extra_email_username", AutoLoginModel::class.java)
            if (data != null) {
                userLogin(data.email.toString(), data.password.toString())
            }
        } else {
            @Suppress("DEPRECATION")
            val data = intent.getParcelableExtra<AutoLoginModel>("extra_email_username")
            if (data != null) {
                userLogin(data.email.toString(), data.password.toString())
            }
        }
    }


}