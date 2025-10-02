package com.example.quickauth

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.quickauth.databinding.ActivityOtpVerificationBinding
import com.example.quickauth.models.User
import com.example.quickauth.services.FirebaseAuthService
import com.example.quickauth.services.LocalNotificationService
import com.example.quickauth.utils.PhoneUtils
import com.example.quickauth.utils.SessionManager
import com.google.firebase.FirebaseException

class OtpVerificationActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityOtpVerificationBinding
    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 120000 // 2 minutes
    private var isTimerRunning = false
    private var phoneNumber: String = ""
    private var actualPhoneNumber: String = ""
    private var isSignUp: Boolean = false
    private val firebaseAuthService = FirebaseAuthService()
    private lateinit var sessionManager: SessionManager
    private lateinit var otpBoxes: List<EditText>
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityOtpVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        sessionManager = SessionManager(this)
        
        // Initialize OTP boxes list
        otpBoxes = listOf(
            binding.etOtp1, binding.etOtp2, binding.etOtp3,
            binding.etOtp4, binding.etOtp5, binding.etOtp6
        )
        
        getIntentData()
        setupUI()
        setupClickListeners()
        setupBackPressedCallback()
        setupOtpBoxes()
        startTimer()
        setupAnimations()
    }
    
    private fun getIntentData() {
        phoneNumber = intent.getStringExtra("phone_number") ?: ""
        actualPhoneNumber = intent.getStringExtra("actual_phone_number") ?: phoneNumber
        isSignUp = intent.getBooleanExtra("is_sign_up", false)
        
        // Display masked phone number
        if (phoneNumber.isNotEmpty()) {
            binding.tvPhoneNumber.text = PhoneUtils.maskPhoneNumber(phoneNumber)
        }
    }
    
    private fun setupUI() {
        // Update subtitle based on sign up or sign in
        val subtitle = if (isSignUp) {
            "We've sent a verification code to"
        } else {
            "We've sent a verification code to"
        }
        binding.tvSubtitle.text = subtitle
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        
        binding.btnVerify.setOnClickListener {
            if (validateOtp()) {
                verifyOtp()
            }
        }
        
        binding.btnResend.setOnClickListener {
            if (!isTimerRunning) {
                resendOtp()
            }
        }
    }
    
    private fun setupOtpBoxes() {
        otpBoxes.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Hide error when user starts typing
                    binding.tvError.visibility = View.GONE
                    
                    if (s?.length == 1) {
                        // Move to next box
                        if (index < otpBoxes.size - 1) {
                            otpBoxes[index + 1].requestFocus()
                        } else {
                            // Last box filled, hide keyboard
                            editText.clearFocus()
                            // Auto-verify when all boxes are filled
                            if (otpBoxes.all { it.text.toString().isNotEmpty() }) {
                                verifyOtp()
                            }
                        }
                    }
                }
                
                override fun afterTextChanged(s: Editable?) {}
            })
            
            editText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (editText.text.toString().isEmpty() && index > 0) {
                        // Move to previous box on backspace
                        otpBoxes[index - 1].requestFocus()
                        otpBoxes[index - 1].setText("")
                        return@setOnKeyListener true
                    }
                }
                false
            }
        }
        
        // Focus first box
        otpBoxes[0].requestFocus()
    }
    
    private fun setupBackPressedCallback() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
        })
    }
    
    private fun validateOtp(): Boolean {
        val otp = getOtpFromBoxes()
        
        if (otp.isEmpty()) {
            showError("Please enter the OTP")
            return false
        }
        
        if (otp.length != 6) {
            showError("Please enter complete 6-digit OTP")
            return false
        }
        
        return true
    }
    
    private fun getOtpFromBoxes(): String {
        return otpBoxes.joinToString("") { it.text.toString() }
    }
    
    private fun showError(message: String) {
        binding.tvError.text = message
        binding.tvError.visibility = View.VISIBLE
    }
    
    private fun hideError() {
        binding.tvError.visibility = View.GONE
    }
    
    private fun verifyOtp() {
        val otp = getOtpFromBoxes()
        
        binding.progressBar.visibility = View.VISIBLE
        binding.btnVerify.isEnabled = false
        binding.btnVerify.text = ""
        
        // Verify OTP with Firebase
        firebaseAuthService.verifyOTP(otp) { isSuccess, error ->
            binding.progressBar.visibility = View.GONE
            binding.btnVerify.isEnabled = true
            binding.btnVerify.text = "Verify OTP"
            
            if (isSuccess) {
                if (isSignUp) {
                    saveUserToFirestore()
                } else {
                    val currentUser = firebaseAuthService.getCurrentUser()
                    if (currentUser != null) {
                        firebaseAuthService.getUserFromFirestore(currentUser.uid) { user, error ->
                        if (user != null) {
                            sessionManager.saveUserSession(
                                userId = user.uid,
                                userName = user.fullName,
                                userEmail = user.email,
                                userPhone = user.phoneNumber
                            )
                            // Show welcome back notification
                            LocalNotificationService.showWelcomeBackNotification(this@OtpVerificationActivity, user.fullName)
                        }
                        navigateToDashboard("Login successful!")
                        }
                    } else {
                        // Show welcome back notification for existing user
                        val userName = sessionManager.getUserName() ?: "User"
                        LocalNotificationService.showWelcomeBackNotification(this@OtpVerificationActivity, userName)
                        navigateToDashboard("Login successful!")
                    }
                }
            } else {
                showError(error ?: "Invalid OTP. Please try again.")
                clearOtpBoxes()
            }
        }
    }
    
    private fun clearOtpBoxes() {
        otpBoxes.forEach { it.setText("") }
        otpBoxes[0].requestFocus()
    }
    
    private fun saveUserToFirestore() {
        // Get user data from intent
        val fullName = intent.getStringExtra("full_name") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        
        // Get the authenticated user's UID from Firebase Auth
        val currentUser = firebaseAuthService.getCurrentUser()
        if (currentUser == null) {
            binding.progressBar.visibility = View.GONE
            binding.btnVerify.isEnabled = true
            binding.btnVerify.text = "Verify OTP"
            showError("Authentication failed. Please try again.")
            return
        }
        
        val user = User(
            uid = currentUser.uid,
            fullName = fullName,
            email = email,
            phoneNumber = actualPhoneNumber,
            isVerified = true
        )
        
        // Save user data to Firestore with Firebase Auth
        firebaseAuthService.saveUserToFirestore(user) { isSuccess, error ->
            binding.progressBar.visibility = View.GONE
            if (isSuccess) {
                // Save user session
                sessionManager.saveUserSession(
                    userId = user.uid,
                    userName = user.fullName,
                    userEmail = user.email,
                    userPhone = user.phoneNumber
                )
                // Show welcome notification for new user
                LocalNotificationService.showWelcomeNotification(this@OtpVerificationActivity, user.fullName)
                navigateToDashboard("Registration successful!")
            } else {
                binding.btnVerify.isEnabled = true
                binding.btnVerify.text = "Verify OTP"
                showError("Failed to save user: ${error ?: "Unknown error"}")
            }
        }
    }
    
    private fun navigateToDashboard(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }
    
    private fun resendOtp() {
        binding.btnResend.isEnabled = false
        binding.tvError.visibility = View.GONE
        
        firebaseAuthService.resendOTP(actualPhoneNumber, this, getResendCallbacks())
    }
    
    private fun getResendCallbacks() = object : com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: com.google.firebase.auth.PhoneAuthCredential) {
            // Auto-verification completed
        }

        override fun onVerificationFailed(e: FirebaseException) {
            binding.btnResend.isEnabled = true
            showError("Failed to resend OTP: ${e.message}")
        }

        override fun onCodeSent(verificationId: String, token: com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken) {
            FirebaseAuthService.verificationId = verificationId
            FirebaseAuthService.resendToken = token
            
            Toast.makeText(this@OtpVerificationActivity, "OTP sent successfully!", Toast.LENGTH_SHORT).show()
            clearOtpBoxes()
            startTimer()
            binding.btnResend.isEnabled = false
        }
    }
    
    private fun startTimer() {
        countDownTimer?.cancel()
        timeLeftInMillis = 120000 // Reset to 2 minutes
        isTimerRunning = true
        binding.btnResend.isEnabled = false
        
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimer()
            }
            
            override fun onFinish() {
                isTimerRunning = false
                binding.btnResend.isEnabled = true
            }
        }.start()
    }
    
    private fun updateTimer() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        
        val timeLeftFormatted = String.format("%02d:%02d", minutes, seconds)
        binding.tvTimer.text = timeLeftFormatted
    }
    
    private fun setupAnimations() {
        // Initial entrance animations
        binding.ivSecurityIcon.alpha = 0f
        binding.ivSecurityIcon.translationY = -50f
        
        binding.tvTitle.alpha = 0f
        binding.tvTitle.translationY = 30f
        
        binding.tvSubtitle.alpha = 0f
        binding.tvSubtitle.translationY = 30f
        
        binding.tvPhoneNumber.alpha = 0f
        binding.tvPhoneNumber.translationY = 30f
        
        binding.llOtpContainer.alpha = 0f
        binding.llOtpContainer.translationY = 50f
        
        binding.btnVerify.alpha = 0f
        binding.btnVerify.translationY = 30f
        
        binding.llTimer.alpha = 0f
        binding.llTimer.translationY = 30f
        
        binding.llResendSection.alpha = 0f
        binding.llResendSection.translationY = 30f
        
        // Animate security icon
        binding.ivSecurityIcon.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(800)
            .setStartDelay(200)
            .start()
        
        // Animate title
        binding.tvTitle.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .setStartDelay(400)
            .start()
        
        // Animate subtitle
        binding.tvSubtitle.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .setStartDelay(500)
            .start()
        
        // Animate phone number
        binding.tvPhoneNumber.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .setStartDelay(600)
            .start()
        
        // Animate OTP container
        binding.llOtpContainer.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(800)
            .setStartDelay(800)
            .start()
        
        // Animate timer
        binding.llTimer.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .setStartDelay(1000)
            .start()
        
        // Animate verify button
        binding.btnVerify.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .setStartDelay(1200)
            .start()
        
        // Animate resend section
        binding.llResendSection.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .setStartDelay(1400)
            .start()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
