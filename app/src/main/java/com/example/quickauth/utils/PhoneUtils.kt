package com.example.quickauth.utils

object PhoneUtils {
    
    /**
     * Format phone number with country code if not present
     */
    fun formatPhoneNumber(phone: String): String {
        // Remove all non-digit characters except +
        val cleaned = phone.replace(" ", "").replace("-", "").replace("(", "").replace(")", "")
        
        return when {
            cleaned.startsWith("+91") && cleaned.length == 13 -> cleaned // Already properly formatted
            cleaned.startsWith("+91") && cleaned.length < 13 -> {
                // Add missing digits if needed
                val digits = cleaned.substring(3)
                if (digits.length == 10) {
                    cleaned
                } else {
                    "+91$digits"
                }
            }
            cleaned.startsWith("91") && cleaned.length == 12 -> "+$cleaned"
            cleaned.startsWith("91") && cleaned.length < 12 -> {
                val digits = cleaned.substring(2)
                if (digits.length == 10) {
                    "+91$digits"
                } else {
                    "+91$digits"
                }
            }
            cleaned.startsWith("+") -> cleaned
            cleaned.length == 10 -> "+91$cleaned"
            else -> "+91$cleaned"
        }
    }
    
    /**
     * Mask phone number for display (e.g., 91******89)
     */
    fun maskPhoneNumber(phoneNumber: String): String {
        return try {
            if (phoneNumber.length >= 8) {
                // Remove +91 prefix and show first 2 + masked + last 2
                val cleanNumber = if (phoneNumber.startsWith("+91")) {
                    phoneNumber.substring(3) // Remove +91
                } else if (phoneNumber.startsWith("91")) {
                    phoneNumber.substring(2) // Remove 91
                } else {
                    phoneNumber
                }
                
                if (cleanNumber.length >= 6) {
                    val start = cleanNumber.substring(0, 2) // First 2 digits
                    val end = cleanNumber.substring(cleanNumber.length - 2) // Last 2 digits
                    val maskedLength = cleanNumber.length - 4
                    val masked = "*".repeat(maskedLength)
                    "$start$masked$end"
                } else {
                    phoneNumber
                }
            } else {
                phoneNumber
            }
        } catch (e: Exception) {
            phoneNumber
        }
    }
    
    /**
     * Validate phone number format
     */
    fun isValidPhoneNumber(phone: String): Boolean {
        val cleaned = phone.replace("+", "").replace(" ", "").replace("-", "").replace("(", "").replace(")", "")
        return when {
            cleaned.startsWith("91") && cleaned.length == 12 -> true
            cleaned.length == 10 && cleaned.all { it.isDigit() } -> true
            else -> false
        }
    }
    
    /**
     * Validate E.164 format specifically for Firebase
     */
    fun isValidE164Format(phone: String): Boolean {
        return phone.startsWith("+") && phone.length >= 12 && phone.length <= 15 && 
               phone.substring(1).all { it.isDigit() }
    }
}
