package com.example.pickmeup

import com.squareup.moshi.JsonClass

class Session(var id : Int,
              var name : String,
              var email : String,
              var session_token : String,
              var session_expiration : String,
              var update_token : String)

class RegisterUser (var success : Boolean, var data : Session)

@JsonClass (generateAdapter = true)
data class LoginUser(var success : Boolean, var data : Session) {
    fun getStatus() : Boolean {
        return success
    }

    fun getSession() : Session {
        return data
    }
}