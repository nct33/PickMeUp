package com.example.pickmeup

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.green
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var menu : BottomNavigationView
    private lateinit var appBarConfiguration : AppBarConfiguration
    private lateinit var topToolbar : Toolbar
    private lateinit var drawerLayout : DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        topToolbar = findViewById(R.id.toolbar)
        topToolbar.setTitleTextColor(Color.WHITE)

        setSupportActionBar(topToolbar)

        drawerLayout = findViewById(R.id.drawer_layout)

        val drawerButton : ActionBarDrawerToggle = ActionBarDrawerToggle(this@MainActivity,
                drawerLayout, topToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.setDrawerListener(drawerButton)
        drawerButton.syncState()

        menu = findViewById(R.id.tab_layout)

        menu.setOnNavigationItemSelectedListener { it ->
            when(it.itemId){
                R.id.home -> {
                    setFragment(HomeFragment.newInstance())
                }
                R.id.edit -> {
                    setFragment(ContentFragment.newInstance())
                }
            }
            true
        }

        //beginning at the HomeFragment page
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.pager, HomeFragment.newInstance())
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
}