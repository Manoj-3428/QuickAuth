package com.example.quickauth.services

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.quickauth.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class FirebaseAuthService {
    
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    companion object {
        private const val TAG = "FirebaseAuthService"
        var verificationId: String? = null
        var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    }
    
    // Send OTP to phone number
    fun sendOTP(
        phoneNumber: String,
        activity: Activity,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        // Validate phone number format before sending
        if (!phoneNumber.startsWith("+") || phoneNumber.length < 12) {
            Log.e(TAG, "Invalid phone number format: $phoneNumber")
            callbacks.onVerificationFailed(FirebaseException("Invalid phone number format. Please ensure the number is in E.164 format (e.g., +919876543210)"))
            return
        }
        
        Log.d(TAG, "Sending OTP to formatted phone number: $phoneNumber")
        Log.d(TAG, "Phone number length: ${phoneNumber.length}")
        Log.d(TAG, "Phone number starts with +: ${phoneNumber.startsWith("+")}")
        
        // For testing: Use test phone number if billing is not enabled
        val testPhoneNumber = "+16505553434" // Firebase test number
        val finalPhoneNumber = if (phoneNumber == testPhoneNumber) phoneNumber else phoneNumber
        
        try {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()
            
            Log.d(TAG, "PhoneAuthOptions created successfully")
            PhoneAuthProvider.verifyPhoneNumber(options)
            Log.d(TAG, "OTP request initiated for $phoneNumber")
        } catch (e: Exception) {
            Log.e(TAG, "Error creating PhoneAuthOptions or sending OTP", e)
            callbacks.onVerificationFailed(FirebaseException("Failed to initiate OTP request: ${e.message}"))
        }
    }
    
    // Resend OTP
    fun resendOTP(
        phoneNumber: String,
        activity: Activity,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        resendToken?.let { token ->
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .setForceResendingToken(token)
                .build()
            
            PhoneAuthProvider.verifyPhoneNumber(options)
            Log.d(TAG, "OTP resent to $phoneNumber")
        }
    }
    
    // Verify OTP
    fun verifyOTP(otp: String, onComplete: (Boolean, String?) -> Unit) {
        val verificationId = verificationId ?: ""
        if (verificationId.isEmpty()) {
            onComplete(false, "Verification ID not found. Please request OTP again.")
            return
        }
        
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "OTP verification successful")
                    onComplete(true, null)
                } else {
                    Log.e(TAG, "OTP verification failed", task.exception)
                    val errorMessage = when {
                        task.exception?.message?.contains("invalid-verification-code") == true -> 
                            "Invalid OTP. Please check and try again."
                        task.exception?.message?.contains("invalid-verification-id") == true -> 
                            "Verification expired. Please request OTP again."
                        else -> task.exception?.message ?: "OTP verification failed"
                    }
                    onComplete(false, errorMessage)
                }
            }
    }
    
    // Save user data to Firestore (requires Firebase Auth)
    fun saveUserToFirestore(user: User, onComplete: (Boolean, String?) -> Unit) {
        auth.currentUser?.let { firebaseUser ->
            firestore.collection("users")
                .document(firebaseUser.uid)
                .set(user)
                .addOnSuccessListener {
                    Log.d(TAG, "User data saved successfully")
                    onComplete(true, null)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Failed to save user data", exception)
                    onComplete(false, exception.message)
                }
        } ?: run {
            onComplete(false, "User not authenticated")
        }
    }
    
    // Save user data directly to Firestore (without Firebase Auth)
    fun saveUserDirectly(user: User, onComplete: (Boolean, String?) -> Unit) {
        firestore.collection("users")
            .document(user.uid)
            .set(user)
            .addOnSuccessListener {
                Log.d(TAG, "User data saved successfully (direct)")
                onComplete(true, null)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to save user data (direct)", exception)
                onComplete(false, exception.message)
            }
    }
    
    // Get user data from Firestore
    fun getUserFromFirestore(uid: String, onComplete: (User?, String?) -> Unit) {
        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    Log.d(TAG, "User data retrieved successfully")
                    onComplete(user, null)
                } else {
                    Log.d(TAG, "User document does not exist")
                    onComplete(null, "User data not found")
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to get user data", exception)
                onComplete(null, exception.message)
            }
    }
    
    // Get user by email (for sign in)
    fun getUserByEmail(email: String, onComplete: (User?, String?) -> Unit) {
        firestore.collection("users")
            .whereEqualTo("email", email)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty && documents.documents.isNotEmpty()) {
                    val document = documents.documents[0]
                    val user = document.toObject(User::class.java)
                    Log.d(TAG, "User found by email: ${user?.email}")
                    onComplete(user, null)
                } else {
                    Log.d(TAG, "No user found with email: $email")
                    onComplete(null, "User not found with this email")
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to find user by email", exception)
                onComplete(null, exception.message ?: "Failed to find user")
            }
    }
    
    // Sign out
    fun signOut() {
        auth.signOut()
        verificationId = null
        resendToken = null
        Log.d(TAG, "User signed out")
    }
    
    // Get current user
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
    
    // Check if user is signed in
    fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }
}
