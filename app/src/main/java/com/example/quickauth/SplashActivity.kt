package com.example.quickauth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.quickauth.databinding.ActivitySplashBinding
import com.example.quickauth.utils.SessionManager

class SplashActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySplashBinding
    private lateinit var sessionManager: SessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Hide system bars for full screen experience
        hideSystemBars()
        
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        sessionManager = SessionManager(this)
        
        setupBackPressedCallback()
        setupAnimations()
        navigateToNextScreen()
    }
    
    private fun hideSystemBars() {
        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView)
        windowInsetsController?.let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
    
    private fun setupBackPressedCallback() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Disable back button during splash screen
            }
        })
    }
    
    private fun setupAnimations() {
        // Logo fade in animation
        val logoFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        logoFadeIn.duration = 1000
        binding.ivLogo.startAnimation(logoFadeIn)
        
        // App name slide up animation
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        slideUp.duration = 800
        slideUp.startOffset = 500
        binding.tvAppName.startAnimation(slideUp)
        
        // Tagline slide up animation
        val taglineSlideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        taglineSlideUp.duration = 800
        taglineSlideUp.startOffset = 700
        binding.tvTagline.startAnimation(taglineSlideUp)
        
        // Progress bar fade in animation
        val progressFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        progressFadeIn.duration = 600
        progressFadeIn.startOffset = 1200
        progressFadeIn.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
            override fun onAnimationStart(animation: android.view.animation.Animation?) {}
            override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                binding.progressBar.visibility = android.view.View.VISIBLE
            }
            override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
        })
        binding.progressBar.startAnimation(progressFadeIn)
    }
    
    private fun navigateToNextScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
            // Check if user is already logged in
            if (sessionManager.isLoggedIn()) {
                // User is logged in, go to dashboard
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
            } else {
                // User not logged in, go to auth
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
            }
            finish()
        }, 3000) // 3 seconds splash screen
    }
    
}
