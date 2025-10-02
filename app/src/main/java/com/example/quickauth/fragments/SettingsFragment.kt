package com.example.quickauth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.quickauth.databinding.FragmentSettingsBinding
import com.example.quickauth.utils.SessionManager

class SettingsFragment : Fragment() {
    
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        sessionManager = SessionManager(requireContext())
        
        loadUserData()
        setupClickListeners()
        setupAnimations()
    }
    
    private fun loadUserData() {
        // Get user data from session
        val userName = sessionManager.getUserName() ?: "User"
        val userEmail = sessionManager.getUserEmail() ?: "No email"
        val userPhone = sessionManager.getUserPhone() ?: "No phone number"
        
        // Display user data
        binding.tvUserName.text = userName
        binding.tvUserEmail.text = userEmail
        binding.tvUserPhone.text = userPhone
    }
    
    private fun setupClickListeners() {
        // Profile card click
        binding.cardProfile.setOnClickListener {
            Toast.makeText(context, "Profile settings coming soon!", Toast.LENGTH_SHORT).show()
        }
        
        // Settings items click listeners
        binding.llSettings.getChildAt(0).setOnClickListener {
            Toast.makeText(context, "Profile settings", Toast.LENGTH_SHORT).show()
        }
        
        binding.llSettings.getChildAt(2).setOnClickListener {
            Toast.makeText(context, "Security settings", Toast.LENGTH_SHORT).show()
        }
        
        binding.llSettings.getChildAt(3).setOnClickListener {
            Toast.makeText(context, "About QuickAuth", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupAnimations() {
        // Staggered entrance animations
        binding.cardProfile.alpha = 0f
        binding.cardProfile.translationY = 50f
        binding.cardProfile.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(600)
            .start()
        
        binding.tvSettingsTitle.alpha = 0f
        binding.tvSettingsTitle.translationY = 30f
        binding.tvSettingsTitle.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(200)
            .setDuration(600)
            .start()
        
        binding.llSettings.alpha = 0f
        binding.llSettings.translationY = 30f
        binding.llSettings.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(400)
            .setDuration(600)
            .start()
        
        binding.tvVersion.alpha = 0f
        binding.tvVersion.animate()
            .alpha(1f)
            .setStartDelay(600)
            .setDuration(600)
            .start()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
