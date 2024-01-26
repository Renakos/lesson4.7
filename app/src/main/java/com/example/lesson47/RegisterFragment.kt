package com.example.lesson47

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.lesson47.databinding.FragmentRegisterBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val auth = Firebase.auth
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
        verifyUserPhoneNumber()
    }

    private fun verifyUserPhoneNumber() = with(binding) {
        btnSendCode.setOnClickListener {
            val phoneNumber = etPhoneNumber.text.toString().trim()
            if (validatePhoneNumber(phoneNumber)) {
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(phoneNumber) // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(requireActivity()) // Activity (for callback binding)
                    .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
        }
    }

    private fun validatePhoneNumber(phoneNumber: String): Boolean = with(binding) {
        if (phoneNumber.isEmpty()) {
            tilPhoneNumber.isErrorEnabled = true
            tilPhoneNumber.error = getString(R.string.fill_field_error)
            false
        } else {
            true
        }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.i("success", "onVerificationCompleted:$credential")
            auth.signInWithCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.e("failure", "onVerificationFailed", e)
            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                // reCAPTCHA verification attempted with null Activity
            }
            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            Log.d("code", "onCodeSent:$verificationId")
            storedVerificationId = verificationId
            resendToken = token
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
import androidx.navigation.fragment.findNavController

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val auth = Firebase.auth
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
        verifyUserPhoneNumber()
    }

    private fun verifyUserPhoneNumber() = with(binding) {
        btnSendCode.setOnClickListener {
            val phoneNumber = etPhoneNumber.text.toString().trim()
            if (validatePhoneNumber(phoneNumber)) {
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(phoneNumber) // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(requireActivity()) // Activity (for callback binding)
                    .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
        }
    }

    private fun validatePhoneNumber(phoneNumber: String): Boolean = with(binding) {
        if (phoneNumber.isEmpty()) {
            tilPhoneNumber.isErrorEnabled = true
            tilPhoneNumber.error = getString(R.string.fill_field_error)
            false
        } else {
            true
        }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.i("success", "onVerificationCompleted:$credential")
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Пользователь успешно аутентифицирован
                        // Выполните дополнительные действия после успешной аутентификации
                        // Пример: переход на HomeFragment
                        findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                    } else {
                        // Ошибка аутентификации
                        Log.e("failure", "signInWithCredential:failure", task.exception)
                    }
                }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.e("failure", "onVerificationFailed", e)
            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                // reCAPTCHA verification attempted with null Activity
            }
            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            Log.d("code", "onCodeSent:$verificationId")
            storedVerificationId = verificationId
            resendToken = token
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = _binding!!
    private val adapter = Adapter()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.rv.adapter= adapter
        adapter.setData(
            listOf(
                ItemModel(
                    0,
                    getString(R.string.n2d),
                    getString(R.string.work),
                    getString(R.string.n2d),
                    getString(R.string.work)
                )
            ))
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".ui.fragments.home.HomeFragment"
    android:background="@color/black">
    <androidx.cardview.widget.CardView
        android:layout_width="match_parent"
        android:layout_height="50dp"
        android:backgroundTint="@color/black">
        <ImageView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginStart="20dp"
            android:layout_marginTop="20dp"
            android:src="@drawable/menu"/>
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginStart="160dp"
            android:layout_marginTop="10dp"
            android:textSize="20sp"
            android:text="@string/ame"
            android:textColor="@color/white"
            android:textStyle="bold"/>
        <ImageView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginStart="370dp"
            android:layout_marginTop="20dp"
            android:src="@drawable/shape"/>
    </androidx.cardview.widget.CardView>
    <androidx.cardview.widget.CardView
        android:layout_width="320dp"
        android:layout_height="40dp"
        android:layout_marginTop="20dp"
        android:layout_marginStart="40dp"
        android:backgroundTint="@color/gray"
        app:cardCornerRadius="18dp">
        <ImageView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:src="@drawable/shape_1_"
            android:layout_marginTop="12dp"
            android:layout_marginStart="20dp"/>
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textColor="@color/white"
            android:layout_marginStart="50dp"
            android:text="@string/search"
            android:layout_marginTop="10dp"/>
    </androidx.cardview.widget.CardView>
    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/rv"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            tools:listitem="@layout/item"
            app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager"/>

    </FrameLayout>

</LinearLayout>
