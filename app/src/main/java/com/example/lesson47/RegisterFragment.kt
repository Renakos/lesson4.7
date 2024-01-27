package com.example.lesson47

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lesson47.databinding.FragmentRegisterBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseAuth.getInstance().currentUser?.let {

            findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
        } ?: run {

            setupVerification()
        }

    }

    private fun setupVerification() {
        with(binding) {
            btnSendCode.setOnClickListener {
                val phoneNumber = etPhoneNumber.text.toString().trim()
                if (validatePhoneNumber(phoneNumber)) {
                    val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(requireActivity())
                        .setCallbacks(callbacks)
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)
                }
            }
        }
    }

    private fun validatePhoneNumber(phoneNumber: String): Boolean {
        return if (phoneNumber.isEmpty()) {
            binding.tilPhoneNumber.isErrorEnabled = true
            binding.tilPhoneNumber.error = getString(R.string.fill_field_error)
            false
        } else {
            true
        }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.i("success", "onVerificationCompleted:$credential")
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                    } else {
                        Log.e("failure", "signInWithCredential:failure", task.exception)
                    }
                }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.e("failure", "onVerificationFailed", e)


        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            Log.d("code", "onCodeSent:$verificationId")
            storedVerificationId = verificationId
            resendToken = token
            navigateToHomeFragmentIfCodeSent()
        }
    }

    private fun navigateToHomeFragmentIfCodeSent() {

        if (::storedVerificationId.isInitialized) {
            findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
