package com.example.pickmeup

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var sideMenu : NavigationView
    private lateinit var menu : BottomNavigationView
    private lateinit var appBarConfiguration : AppBarConfiguration
    private lateinit var topToolbar : Toolbar
    private lateinit var drawerLayout : DrawerLayout

    private lateinit var header : View
    private lateinit var name : TextView
    private lateinit var email : TextView

    private val PICK_IMAGE = 100

    private lateinit var imageUri : Uri
    private lateinit var profilePic : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        topToolbar = findViewById(R.id.toolbar)
        topToolbar.setTitleTextColor(Color.WHITE)
        topToolbar.setSubtitle(R.string.description)
        topToolbar.setSubtitleTextColor(Color.WHITE)

        setSupportActionBar(topToolbar)

        drawerLayout = findViewById(R.id.drawer_layout)

        val drawerButton : ActionBarDrawerToggle = ActionBarDrawerToggle(this@MainActivity,
                drawerLayout, topToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.setDrawerListener(drawerButton)
        drawerButton.syncState()

        sideMenu = findViewById(R.id.nav_view)
        header = sideMenu.getHeaderView(0)

        profilePic = header.findViewById(R.id.imageView)
        name = header.findViewById(R.id.username)
        email = header.findViewById(R.id.email)

        name.text = UserRepository.getSession()!!.name
        email.text = UserRepository.getSession()!!.email

        sideMenu.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_change_image -> {
                    openGallery()
                    true
                }
                R.id.nav_change_name -> {
                    true
                }
                else -> {
                    false
                }
            }
        }

        menu = findViewById(R.id.tab_layout)

        menu.setOnNavigationItemSelectedListener { it ->
            when(it.itemId){
                R.id.edit -> {
                    setFragment(ContentFragment.newInstance())
                }
                R.id.home -> {
                    setFragment(HomeFragment.newInstance())
                    //val dummy_session_token = "0d86e33051df5d0f5df9c0b2f92ab5325b5d9acb"
                    getPreferredFeed(UserRepository.getSession()!!.session_token,
                        UserRepository.getSession()!!.id)
                }
            }
            true
        }

        //beginning at the ContentFragment page
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.pager, ContentFragment.newInstance())
            commit()
        }
    }

    private fun setFragment(fragment : Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.pager, fragment)
            commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.profile_sidebar, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private val client = OkHttpClient()

    private fun getImages(authorization : String, category : String, userID : Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val info ="""{"authorization": "$authorization", "category": "$category"}"""

            val body = info.toRequestBody("application/json; charset=utf-8".toMediaType())

            val request: Request = Request.Builder()
                .url("https://work-pvnxn5ufaq-uc.a.run.app/api/data/$userID/")
                .post(body)
                .build()

            withContext(Dispatchers.IO) {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    for ((name, value) in response.headers) {
                        println("$name: $value")
                    }

                    val response_feed = response.body!!.string()

                    val moshi = Moshi.Builder()
                        .addLast(KotlinJsonAdapterFactory())
                        .build()
                    val adapter = moshi.adapter(ImageObtain::class.java)

                    val feed = adapter.fromJson(response_feed)
                    for(i in feed!!.data.indices) {
                        FeedRepository.addFeed(imageToFeed(feed.data[i]))
                    }
                }
            }
        }
    }

    private fun getPreferredFeed(authorization : String, userID : Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val info ="""{"authorization": "$authorization"}"""

            val body = info.toRequestBody("application/json; charset=utf-8".toMediaType())

            val request: Request = Request.Builder()
                .url("https://work-pvnxn5ufaq-uc.a.run.app/api/data/$userID/")
                .post(body)
                .build()

            withContext(Dispatchers.IO) {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    for ((name, value) in response.headers) {
                        println("$name: $value")
                    }

                    val responseFeed = response.body!!.string()
                    println(responseFeed)

                    val moshi = Moshi.Builder()
                        .addLast(KotlinJsonAdapterFactory())
                        .build()
                    val adapter = moshi.adapter(ImageObtain::class.java)

                    val feed = adapter.fromJson(responseFeed)

                    for(i in feed!!.data.indices) {
                        FeedRepository.addFeed(imageToFeed(feed.data[i]))
                    }
                }
            }
        }
    }

    private fun imageToFeed(image: Image) : Feed {
        return Feed(image.photographer,
            image.category,
            image.photo
        )
    }

    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data?.data!!
            profilePic.setImageURI(imageUri)
        }
    }
}