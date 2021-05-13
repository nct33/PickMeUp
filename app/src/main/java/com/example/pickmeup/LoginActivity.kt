package com.example.pickmeup

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class LoginActivity : AppCompatActivity() {

    private lateinit var login : Button
    private lateinit var register : Button

    private lateinit var loginDialog : AlertDialog.Builder

    private lateinit var username : EditText
    private lateinit var password : EditText
    private lateinit var email : EditText

    private lateinit var background : ImageView

    private val imageArray : Array<Int> = arrayOf(R.drawable.relaxing_1, R.drawable.relaxing_2,
        R.drawable.relaxing_3, R.drawable.relaxing_4, R.drawable.relaxing_5,
    )

    private var check : Boolean = true
    private var count : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        UserRepository.empty()

        background = findViewById(R.id.login_background)

        val handler = Handler()
        val runnable: Runnable = object : Runnable {
            var i = 0
            override fun run() {
                background.setImageResource(imageArray.get(i))
                i++
                if (i > imageArray.size - 1) {
                    i = 0
                }
                handler.postDelayed(this, 10000)
            }
        }
        handler.postDelayed(runnable, 10000)

        login = findViewById(R.id.login_button)
        register = findViewById(R.id.register_button)

        //Login Button functions
        login.setOnClickListener {
            loginDialog = AlertDialog.Builder(this)
            loginDialog.setTitle("Login here")

            val viewGroup : ViewGroup = findViewById(android.R.id.content);
            val dialogView : View = LayoutInflater.from(this@LoginActivity).inflate(R.layout.login_alert, viewGroup, false);
            loginDialog.setView(dialogView)
            email = dialogView.findViewById(R.id.email_login)
            password = dialogView.findViewById(R.id.password_login)

            loginDialog.setPositiveButton("Log in") { _: DialogInterface, _: Int ->
                logIn(email.text.toString(), password.text.toString())
                if(check && count > 0) {
                    Toast.makeText(this@LoginActivity, "Login not successful, " +
                            "Please try again", Toast.LENGTH_SHORT
                    ).show()
                    count=1
                }
            }

            loginDialog.setNegativeButton("Cancel") { cancel: DialogInterface, _: Int ->
                cancel.dismiss()
            }

            loginDialog.show()
        }

        //Register Button Functions
        register.setOnClickListener {
            loginDialog = AlertDialog.Builder(this)
            loginDialog.setTitle("Register here")

            val viewGroup : ViewGroup = findViewById(android.R.id.content);
            val dialogView : View = LayoutInflater.from(this@LoginActivity).inflate(R.layout.register_alert, viewGroup, false);
            loginDialog.setView(dialogView)
            username = dialogView.findViewById(R.id.name_registery)
            email = dialogView.findViewById(R.id.email_registry)
            password = dialogView.findViewById(R.id.password_registry)

            loginDialog.setPositiveButton("Register", DialogInterface.OnClickListener{
                    _: DialogInterface, _: Int ->
                register(username.text.toString(), email.text.toString(), password.text.toString())
                if(check && count > 0) {
                    Toast.makeText(this@LoginActivity, "Register not successful, " +
                            "Please try again", Toast.LENGTH_SHORT
                    ).show()
                    count=1
                }
            })

            loginDialog.setNegativeButton("Cancel", DialogInterface.OnClickListener{
                    cancel : DialogInterface, _: Int ->
                cancel.dismiss()
            })

            loginDialog.show()
        }
    }

    private val client = OkHttpClient()

    private fun register(name : String, email : String, password : String) {

        CoroutineScope(Dispatchers.Main).launch {
            val info ="""{"name": "$name", "email": "$email", "password": "$password"}"""

            val body = info.toRequestBody("application/json; charset=utf-8".toMediaType())

            val request: Request = Request.Builder()
                .url("https://work-pvnxn5ufaq-uc.a.run.app/api/register/")
                .post(body)
                .build()

            withContext(Dispatchers.IO) {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                    } else {
                        for ((name, value) in response.headers) {
                            println("$name: $value")
                        }

                        val response = response.body!!.string()

                        println(response)

                        val moshi = Moshi.Builder()
                            .addLast(KotlinJsonAdapterFactory())
                            .build()
                        val adapter = moshi.adapter(RegisterUser::class.java)

                        val register_response = adapter.fromJson(response)

                        println(register_response!!.success)

                        UserRepository.setUser(
                            Session(
                                register_response.data.id, register_response.data.name,
                                register_response.data.email, register_response.data.session_token,
                                register_response.data.session_expiration,
                                register_response.data.update_token
                            )
                        )
                        if (register_response!!.success) {
                            FeedRepository.emptyFeed()
                            getCategories()
                            val intent: Intent = Intent(
                                this@LoginActivity,
                                MainActivity::class.java
                            )
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    private fun logIn(email : String, password : String) {

        CoroutineScope(Dispatchers.Main).launch {
            val info ="""{"email": "$email", "password": "$password"}"""

            val body = info.toRequestBody("application/json; charset=utf-8".toMediaType())

            val loggingIn: Request = Request.Builder()
                .url("https://work-pvnxn5ufaq-uc.a.run.app/api/login/")
                .post(body)
                .build()

            withContext(Dispatchers.IO) {
                client.newCall(loggingIn).execute().use { response ->
                    if (!response.isSuccessful) {
                        check = true
                    } else {
                        check = false
                        for ((name, value) in response.headers) {
                            println("$name: $value")
                        }

                        val response = response.body!!.string()

                        println(response)

                        val moshi = Moshi.Builder()
                            .addLast(KotlinJsonAdapterFactory())
                            .build()
                        val adapter = moshi.adapter(LoginUser::class.java)

                        val login = adapter.fromJson(response)

                        println(login!!.success)

                        UserRepository.setUser(
                            Session(
                                login.data.id, login.data.name, login.data.email,
                                login.data.session_token, login.data.session_expiration,
                                login.data.update_token
                            )
                        )
                        if (login!!.success) {
                            FeedRepository.emptyFeed()
                            getCategories()
                            getUserCategories(UserRepository.getSession()!!.session_token,
                                UserRepository.getSession()!!.id
                            )
                            val intent: Intent = Intent(
                                this@LoginActivity,
                                MainActivity::class.java
                            )
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    private fun getCategories() {
        CoroutineScope(Dispatchers.Main).launch {
            val request = Request.Builder()
                .url("https://work-pvnxn5ufaq-uc.a.run.app/api/categories/")
                .build()

            withContext(Dispatchers.IO) {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    for ((name, value) in response.headers) {
                        println("$name: $value")
                    }

                    val responseCategories = response.body!!.string()
                    println(responseCategories)

                    val moshi = Moshi.Builder()
                        .addLast(KotlinJsonAdapterFactory())
                        .build()
                    val adapter = moshi.adapter(Tags::class.java)

                    val categories = adapter.fromJson(responseCategories)

                    Repository.setTags(categories!!.getTags())
                }
            }
        }
    }

    private fun getUserCategories(session_token : String, userID : Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val info ="""{"authorization": "$session_token"}"""

            val body = info.toRequestBody("application/json; charset=utf-8".toMediaType())

            val userCategories: Request = Request.Builder()
                .url("https://work-pvnxn5ufaq-uc.a.run.app/api/info/$userID/")
                .post(body)
                .build()

            withContext(Dispatchers.IO) {
                client.newCall(userCategories).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    for ((name, value) in response.headers) {
                        println("$name: $value")
                    }

                    val responseCategories = response.body!!.string()
                    println(responseCategories)

                    val moshi = Moshi.Builder()
                        .addLast(KotlinJsonAdapterFactory())
                        .build()
                    val adapter = moshi.adapter(UserPref::class.java)

                    val categories = adapter.fromJson(responseCategories)

                    CategoryRepository.setTags(categories!!.data.getUserTags())
                    val unusedCategories =
                        Repository.getInstance() subtract CategoryRepository.getInstance()
                    Repository.setTags(unusedCategories.toMutableList())
                }
            }
        }
    }
}