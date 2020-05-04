package com.keksec.bicodit_android.screens.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.keksec.bicodit_android.AppController
import com.keksec.bicodit_android.R
import com.keksec.bicodit_android.screens.authentication.AuthenticationActivity
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), NavListener {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector(): DispatchingAndroidInjector<Fragment> {
        return dispatchingAndroidInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (AppController.prefs.accessToken == "") {
            navigateToAuthenticationActivity()
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
    }

    override fun navigateToAuthenticationActivity() {
        val authIntent = Intent(this, AuthenticationActivity::class.java)
        startActivity(authIntent)
        finish()
    }

    fun hideBottomNav() {
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.visibility = View.GONE
    }

    fun showBottomNav() {
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.visibility = View.VISIBLE
    }
}