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
import com.example.quickauth.databinding.FragmentSignInBinding
import com.example.quickauth.services.FirebaseAuthService
import com.example.quickauth.utils.PhoneUtils
import com.google.firebase.FirebaseException

class SignInFragment : Fragment() {
    
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private val firebaseAuthService = FirebaseAuthService()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
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
        binding.tilPhone.startAnimation(slideUp1)
        
        val slideUp2 = AnimationUtils.loadAnimation(context, R.anim.slide_up)
        slideUp2.startOffset = 1000
        binding.btnSignIn.startAnimation(slideUp2)
        
        val slideUp3 = AnimationUtils.loadAnimation(context, R.anim.slide_up)
        slideUp3.startOffset = 1200
        binding.tvSignUpLink.startAnimation(slideUp3)
    }
    
    private fun setupClickListeners() {
        binding.btnSignIn.setOnClickListener {
            if (validateInputs()) {
                sendOTPForSignIn()
            }
        }
        
        binding.tvSignUpLink.setOnClickListener {
            (activity as? com.example.quickauth.AuthActivity)?.switchToSignUp()
        }
    }
    
    private fun sendOTPForSignIn() {
        val phoneNumber = PhoneUtils.formatPhoneNumber(binding.etPhone.text.toString().trim())
        
        binding.btnSignIn.isEnabled = false
        binding.btnSignIn.text = "Sending OTP..."
        
        firebaseAuthService.sendOTP(phoneNumber, requireActivity(), getOTPCallbacks(phoneNumber))
    }
    
    private fun getOTPCallbacks(phoneNumber: String) = object : com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: com.google.firebase.auth.PhoneAuthCredential) {
            // Auto-verification completed
            binding.btnSignIn.isEnabled = true
            binding.btnSignIn.text = "Sign In"
            Toast.makeText(context, "Auto-verification completed", Toast.LENGTH_SHORT).show()
        }

        override fun onVerificationFailed(e: FirebaseException) {
            binding.btnSignIn.isEnabled = true
            binding.btnSignIn.text = "Sign In"
            Toast.makeText(context, "OTP sending failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        
        override fun onCodeSent(verificationId: String, token: com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken) {
            FirebaseAuthService.verificationId = verificationId
            FirebaseAuthService.resendToken = token
            
            // Navigate to OTP verification with masked phone number
            val maskedPhone = PhoneUtils.maskPhoneNumber(phoneNumber)
            val intent = android.content.Intent(requireContext(), com.example.quickauth.OtpVerificationActivity::class.java).apply {
                putExtra("phone_number", maskedPhone)
                putExtra("actual_phone_number", phoneNumber)
                putExtra("is_sign_up", false)
            }
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            
            binding.btnSignIn.isEnabled = true
            binding.btnSignIn.text = "Sign In"
        }
    }
    
    private fun formatPhoneNumber(phone: String): String {
        // Add country code if not present
        return if (phone.startsWith("+")) {
            phone
        } else if (phone.startsWith("91")) {
            "+$phone"
        } else {
            "+91$phone"
        }
    }
    
    private fun validateInputs(): Boolean {
        val phone = binding.etPhone.text.toString().trim()
        
        clearErrors()
        
        // Validate phone
        if (phone.isEmpty()) {
            binding.tilPhone.error = getString(R.string.phone_required)
            return false
        }
        
        if (!PhoneUtils.isValidPhoneNumber(phone)) {
            binding.tilPhone.error = getString(R.string.invalid_phone)
            return false
        }
        
        return true
    }
    
    private fun clearErrors() {
        binding.tilPhone.error = null
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

