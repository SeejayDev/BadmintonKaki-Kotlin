package com.seejay.badmintonkaki.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.seejay.badmintonkaki.R
import com.seejay.badmintonkaki.databinding.FragmentAccountCreateBinding
import com.seejay.badmintonkaki.utilities.ThisDevice

class AccountCreateFragment: Fragment() {
    private lateinit var navController: NavController
    private lateinit var binding: FragmentAccountCreateBinding
    var auth: FirebaseAuth = Firebase.auth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentAccountCreateBinding>(inflater,
            R.layout.fragment_account_create, container, false)

        navController = findNavController()
        setListeners()

        return binding.root
    }

    fun setListeners() {
        binding.btnRegister.setOnClickListener {
            ThisDevice(it.context).vibrate()

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.txtEmail.text).matches()) {
                showToast("Invalid email format")
            } else {
                if (binding.txtPassword.length() < 6) {
                    showToast("Password should be at least 6 characters long")
                } else {
                    if (!binding.txtPassword.text.toString().equals(binding.txtConfirm.text.toString())) {
                        showToast("Passwords entered do not match")
                    } else {
                        createNewUserAccount(
                            binding.txtEmail.text.toString(),
                            binding.txtPassword.text.toString()
                        )
                    }
                }
            }
        }
    }

    fun createNewUserAccount(email: String, password: String) {
        binding.btnRegister.isEnabled = false
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showToast("Account created!")
                navController.navigate(R.id.action_accountCreateFragment_to_loginFragment)
            } else {
                // If sign in fails, display a message to the user.
                try {
                    throw task.exception!!
                } catch (e:FirebaseAuthUserCollisionException) {
                    showToast("An account with this email exists!")
                }
            }
            binding.btnRegister.isEnabled = true
        }
    }

    fun showToast(error: String) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    }
}