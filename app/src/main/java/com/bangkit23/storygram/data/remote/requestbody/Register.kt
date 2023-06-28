package com.bangkit23.storygram.data.remote.requestbody

data class Register(
    val name: String,
    val email: String,
    val password: String
)
