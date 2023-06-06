package com.seejay.badmintonkaki.ui.fragments

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.seejay.badmintonkaki.R
import com.seejay.badmintonkaki.databinding.FragmentLoginBinding
import com.seejay.badmintonkaki.firebase.FirebaseClass
import com.seejay.badmintonkaki.utilities.ThisDevice

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        // init important variables first
        auth = Firebase.auth
        db = Firebase.firestore
        navController = this.findNavController()

        // set button listeners
        setListeners()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            processLogin(currentUser.uid)
        }

        return binding.root
    }

    private fun setListeners() {
        binding.btnLogin.setOnClickListener{
            if (binding.txtEmail.length() < 1) {
                ThisDevice(this.requireContext()).showToast("Email cannot be empty!")
            } else {
                loginUser(binding.txtEmail.text.toString(), binding.txtPassword.text.toString())
            }
        }

        binding.btnSignup.setOnClickListener{
            navController.navigate(R.id.action_loginFragment_to_accountCreateFragment)
        }

        binding.chkShowPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.txtPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                binding.txtPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            binding.txtPassword.setSelection(binding.txtPassword.text.length)
        }
    }

    fun loginUser(email: String, password: String) {
        binding.btnLogin.isEnabled = false
        binding.btnSignup.isEnabled = false
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success, check if user has a profile already
                ThisDevice(this.requireContext()).showToast("Logged in successfully")

                var uid = ""
                if (task.result.user?.uid != null) {
                    uid = task.result.user!!.uid
                }

                processLogin(uid)

                // navController.navigate(R.id.action_loginFragment_to_myGroupsFragment)
            } else {
                // If sign in fails, display a message to the user.
                ThisDevice(this.requireContext()).showToast("Log in failed, please try again.")
                binding.btnLogin.isEnabled = true
                binding.btnSignup.isEnabled = true
            }
        }
    }

    // used to check if the user has a profile created or not
    fun processLogin(uid: String) {
        val docRef = db.collection("users").document(uid)
        docRef.get().addOnSuccessListener { document->
            if (document.data != null) {
                // if exists
                navController.navigate(R.id.action_loginFragment_to_myGroupsFragment)
            } else {
                // it does not exist
                ThisDevice(this.requireContext()).showToast("Profile not created yet.")
                navController.navigate(R.id.action_loginFragment_to_profileCreateFragment)
            }
            binding.btnLogin.isEnabled = true
            binding.btnSignup.isEnabled = true
        }.addOnFailureListener {
            Log.d("Firebase", it.toString())
            binding.btnLogin.isEnabled = true
            binding.btnSignup.isEnabled = true
        }
    }
}