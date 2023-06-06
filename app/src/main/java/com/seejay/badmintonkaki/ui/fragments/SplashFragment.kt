package com.seejay.badmintonkaki.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.seejay.badmintonkaki.R
import com.seejay.badmintonkaki.databinding.FragmentSessionFeeCreateBinding
import com.seejay.badmintonkaki.databinding.FragmentSplashBinding
import com.seejay.badmintonkaki.utilities.ThisDevice

class SplashFragment: Fragment() {
    private lateinit var binding: FragmentSplashBinding
    private lateinit var navController: NavController
    var auth: FirebaseAuth = Firebase.auth
    var db :FirebaseFirestore = Firebase.firestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentSplashBinding>(inflater,
            R.layout.fragment_splash, container, false)

        navController = findNavController()

        if (auth.currentUser == null) {
            navController.popBackStack()
            navController.navigate(R.id.action_splashFragment_to_loginFragment)
        } else {
            val docRef = db.collection("users").document(auth.currentUser!!.uid)
            docRef.get().addOnSuccessListener { document->
                if (document.data != null) {
                    // if exists
                    navController.navigate(R.id.action_splashFragment_to_myGroupsFragment)
                } else {
                    // it does not exist
                    ThisDevice(this.requireContext()).showToast("Profile not created yet.")
                    navController.navigate(R.id.action_splashFragment_to_profileCreateFragment)
                }
            }.addOnFailureListener {
                Log.d("Firebase", it.toString())
            }
        }

        return binding.root
    }
}