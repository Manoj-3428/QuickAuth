package com.example.quickauth

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.quickauth.databinding.ActivityAuthBinding
import com.example.quickauth.fragments.SignInFragment
import com.example.quickauth.fragments.SignUpFragment
import com.example.quickauth.services.LocalNotificationService
import kotlin.math.abs

class AuthActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAuthBinding
    private lateinit var authPagerAdapter: AuthPagerAdapter
    private var tempUserData: Map<String, String>? = null
    
    // Notification permission launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Permission granted or denied - continue with flow regardless
        // Notifications will only show if permission is granted
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Create notification channel
        LocalNotificationService.createNotificationChannel(this)
        
        // Request notification permission
        requestNotificationPermission()
        
        setupViewPager()
    }
    
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                }
                else -> {
                    // Request permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
    
    private fun setupViewPager() {
        authPagerAdapter = AuthPagerAdapter(this)
        binding.viewPager.adapter = authPagerAdapter
        binding.viewPager.isUserInputEnabled = false // Disable swipe
        
        // Add smooth page transformer
        binding.viewPager.setPageTransformer(SlidePageTransformer())
        
        // Reduce scroll sensitivity for slower transition
        try {
            val recyclerView = binding.viewPager.getChildAt(0)
            recyclerView?.overScrollMode = View.OVER_SCROLL_NEVER
        } catch (e: Exception) {
            // Ignore
        }
    }
    
    // Custom page transformer for smooth fade transition
    private inner class SlidePageTransformer : ViewPager2.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            page.apply {
                when {
                    position < -1 || position > 1 -> {
                        // Page is completely off-screen
                        alpha = 0f
                    }
                    else -> {
                        // Smooth fade effect
                        alpha = 1f - abs(position)
                        
                        // Reset any transformations
                        translationX = 0f
                        scaleX = 1f
                        scaleY = 1f
                    }
                }
            }
        }
    }
    
    fun switchToSignIn() {
        binding.viewPager.setCurrentItem(0, true) // true for smooth animation
    }
    
    fun switchToSignUp() {
        binding.viewPager.setCurrentItem(1, true) // true for smooth animation
    }
    
    fun storeTempUserData(userData: Map<String, String>) {
        tempUserData = userData
    }
    
    fun getTempUserData(): Map<String, String>? {
        return tempUserData
    }
    
    fun clearTempUserData() {
        tempUserData = null
    }
    
    fun navigateToOtpVerification(phoneNumber: String, isSignUp: Boolean) {
        val intent = Intent(this, OtpVerificationActivity::class.java).apply {
            putExtra("phone_number", phoneNumber)
            putExtra("is_sign_up", isSignUp)
            
            // Pass user data if it's sign up
            if (isSignUp && tempUserData != null) {
                putExtra("full_name", tempUserData!!["fullName"])
                putExtra("email", tempUserData!!["email"])
            }
        }
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
    
    fun navigateToOtpVerificationWithActualPhone(maskedPhone: String, actualPhone: String, isSignUp: Boolean) {
        val intent = Intent(this, OtpVerificationActivity::class.java).apply {
            putExtra("phone_number", maskedPhone)
            putExtra("actual_phone_number", actualPhone)
            putExtra("is_sign_up", isSignUp)
            
            // Pass user data if it's sign up
            if (isSignUp && tempUserData != null) {
                putExtra("full_name", tempUserData!!["fullName"])
                putExtra("email", tempUserData!!["email"])
            }
        }
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
    
    private inner class AuthPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 2
        
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> SignInFragment()
                1 -> SignUpFragment()
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }
    }
}
