package com.example.quickauth.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.quickauth.R
import com.example.quickauth.databinding.FragmentSignUpBinding
import com.example.quickauth.services.FirebaseAuthService
import com.example.quickauth.utils.PhoneUtils
import com.google.firebase.FirebaseException

class SignUpFragment : Fragment() {
    
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val firebaseAuthService = FirebaseAuthService()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners()
        startAnimations()
    }
    
    private fun startAnimations() {
        // Logo animation
        val scaleIn = AnimationUtils.loadAnimation(context, R.anim.scale_in)
        scaleIn.startOffset = 200
        binding.ivLogo.startAnimation(scaleIn)
        
        // Title animation
        val fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        fadeIn.startOffset = 400
        binding.tvTitle.startAnimation(fadeIn)
        
        val fadeInSubtitle = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        fadeInSubtitle.startOffset = 600
        binding.tvSubtitle.startAnimation(fadeInSubtitle)
        
        // Form fields animation with increasing delays
        val slideUp1 = AnimationUtils.loadAnimation(context, R.anim.slide_up)
        slideUp1.startOffset = 800
        binding.tilFullName.startAnimation(slideUp1)
        
        val slideUp2 = AnimationUtils.loadAnimation(context, R.anim.slide_up)
        slideUp2.startOffset = 1000
        binding.tilEmail.startAnimation(slideUp2)
        
        val slideUp3 = AnimationUtils.loadAnimation(context, R.anim.slide_up)
        slideUp3.startOffset = 1200
        binding.tilPhone.startAnimation(slideUp3)
        
        val slideUp4 = AnimationUtils.loadAnimation(context, R.anim.slide_up)
        slideUp4.startOffset = 1400
        binding.btnSignUp.startAnimation(slideUp4)
        
        val slideUp5 = AnimationUtils.loadAnimation(context, R.anim.slide_up)
        slideUp5.startOffset = 1600
        binding.tvSignInLink.startAnimation(slideUp5)
    }
    
    private fun setupClickListeners() {
        binding.btnSignUp.setOnClickListener {
            if (validateInputs()) {
                // Send OTP for verification
                sendOTPForSignUp()
            }
        }
        
        binding.tvSignInLink.setOnClickListener {
            (activity as? com.example.quickauth.AuthActivity)?.switchToSignIn()
        }
    }
    
    private fun sendOTPForSignUp() {
        val phoneNumber = binding.etPhone.text.toString().trim()
        val formattedPhone = PhoneUtils.formatPhoneNumber(phoneNumber)
        
        android.util.Log.d("SignUpFragment", "Original phone: $phoneNumber")
        android.util.Log.d("SignUpFragment", "Formatted phone: $formattedPhone")
        android.util.Log.d("SignUpFragment", "Phone length: ${formattedPhone.length}")
        android.util.Log.d("SignUpFragment", "Is valid E164: ${PhoneUtils.isValidE164Format(formattedPhone)}")
        
        binding.btnSignUp.isEnabled = false
        binding.btnSignUp.text = getString(R.string.sending_otp)
        
        firebaseAuthService.sendOTP(formattedPhone, requireActivity(), getOTPCallbacks())
    }
    
    private fun getOTPCallbacks() = object : com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: com.google.firebase.auth.PhoneAuthCredential) {
            // Auto-verification completed
            binding.btnSignUp.isEnabled = true
            binding.btnSignUp.text = getString(R.string.sign_up)
            Toast.makeText(context, "Auto-verification completed", Toast.LENGTH_SHORT).show()
        }
        
        override fun onVerificationFailed(e: FirebaseException) {
            binding.btnSignUp.isEnabled = true
            binding.btnSignUp.text = getString(R.string.sign_up)
            Toast.makeText(context, getString(R.string.otp_sending_failed, e.message), Toast.LENGTH_SHORT).show()
        }
        
        override fun onCodeSent(verificationId: String, token: com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken) {
            FirebaseAuthService.verificationId = verificationId
            FirebaseAuthService.resendToken = token
            
            val fullName = binding.etFullName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val formattedPhone = PhoneUtils.formatPhoneNumber(binding.etPhone.text.toString().trim())
            val maskedPhone = PhoneUtils.maskPhoneNumber(formattedPhone)
            
            // Navigate to OTP screen with user data
            val intent = android.content.Intent(requireContext(), com.example.quickauth.OtpVerificationActivity::class.java).apply {
                putExtra("phone_number", maskedPhone)
                putExtra("actual_phone_number", formattedPhone)
                putExtra("is_sign_up", true)
                putExtra("full_name", fullName)
                putExtra("email", email)
            }
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            
            binding.btnSignUp.isEnabled = true
            binding.btnSignUp.text = getString(R.string.sign_up)
        }
    }
    
    private fun validateInputs(): Boolean {
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        
        // Clear previous errors
        clearErrors()
        
        var isValid = true
        
        if (TextUtils.isEmpty(fullName)) {
            binding.tilFullName.error = getString(R.string.full_name_required)
            isValid = false
        }
        
        if (TextUtils.isEmpty(email)) {
            binding.tilEmail.error = getString(R.string.email_required)
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.invalid_email)
            isValid = false
        }
        
        if (TextUtils.isEmpty(phone)) {
            binding.tilPhone.error = getString(R.string.phone_required)
            isValid = false
        } else if (!PhoneUtils.isValidPhoneNumber(phone)) {
            binding.tilPhone.error = getString(R.string.invalid_phone)
            isValid = false
        }
        
        return isValid
    }
    
    private fun clearErrors() {
        binding.tilFullName.error = null
        binding.tilEmail.error = null
        binding.tilPhone.error = null
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
