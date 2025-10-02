package com.example.quickauth

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.quickauth.databinding.ActivityDashboardBinding
import com.example.quickauth.fragments.HomeFragment
import com.example.quickauth.fragments.NotificationsFragment
import com.example.quickauth.fragments.SettingsFragment
import com.example.quickauth.services.FirebaseAuthService
import com.example.quickauth.utils.SessionManager

class DashboardActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityDashboardBinding
    private val firebaseAuthService = FirebaseAuthService()
    private lateinit var sessionManager: SessionManager
    private var notificationCount = 3 // Initial count
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        sessionManager = SessionManager(this)
        
        setupToolbar()
        setupBottomNavigation()
        setupInitialFragment()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "QuickAuth"
    }
    
    private fun setupBottomNavigation() {
        // Set initial badge count
        updateNotificationBadge(notificationCount)
        
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    binding.toolbar.title = "Home"
                    true
                }
                R.id.nav_notifications -> {
                    replaceFragment(NotificationsFragment())
                    binding.toolbar.title = "Notifications"
                    true
                }
                R.id.nav_settings -> {
                    replaceFragment(SettingsFragment())
                    binding.toolbar.title = "Settings"
                    true
                }
                else -> false
            }
        }
    }
    
    private fun setupInitialFragment() {
        binding.bottomNavigation.selectedItemId = R.id.nav_home
        replaceFragment(HomeFragment())
        binding.toolbar.title = "Home"
    }
    
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showLogoutDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                logout()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }
    
    private fun logout() {
        firebaseAuthService.signOut()
        sessionManager.clearSession()
        
        val intent = Intent(this, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }
    
    fun updateNotificationBadge(count: Int) {
        notificationCount = count
        val badge = binding.bottomNavigation.getOrCreateBadge(R.id.nav_notifications)
        if (count > 0) {
            badge.number = count
            badge.isVisible = true
        } else {
            badge.isVisible = false
        }
    }
}
