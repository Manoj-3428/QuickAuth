package com.example.quickauth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quickauth.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Add any additional setup if needed
        setupAnimations()
    }
    
    private fun setupAnimations() {
        // Add entrance animations for the cards
        binding.cardWelcome.alpha = 0f
        binding.cardWelcome.animate()
            .alpha(1f)
            .setDuration(500)
            .start()
        
        binding.tvFeaturesTitle.alpha = 0f
        binding.tvFeaturesTitle.animate()
            .alpha(1f)
            .setStartDelay(200)
            .setDuration(500)
            .start()
        
        binding.llFeatures.alpha = 0f
        binding.llFeatures.animate()
            .alpha(1f)
            .setStartDelay(400)
            .setDuration(500)
            .start()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
