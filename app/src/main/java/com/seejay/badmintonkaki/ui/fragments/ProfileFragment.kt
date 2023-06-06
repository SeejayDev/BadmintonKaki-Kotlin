package com.seejay.badmintonkaki.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.seejay.badmintonkaki.R
import com.seejay.badmintonkaki.databinding.FragmentProfileBinding
import com.seejay.badmintonkaki.models.UserProfile
import com.seejay.badmintonkaki.utilities.GlideLoader
import com.seejay.badmintonkaki.utilities.ThisDevice

class ProfileFragment: Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var navController: NavController
    private val db = Firebase.firestore
    var auth: FirebaseAuth = Firebase.auth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentProfileBinding>(inflater,
            R.layout.fragment_profile, container, false)

        navController = findNavController()
        setListeners()
        loadProfile()

        return binding.root
    }

    fun setListeners() {
        binding.btnLogout.setOnClickListener {
            Firebase.auth.signOut()
            navController.popBackStack()
            navController.navigate(R.id.action_global_loginFragment)
        }

        binding.btnEdit.setOnClickListener {
            navController.navigate(R.id.action_profileFragment_to_profileUpdateFragment)
        }
    }

    fun loadProfile() {
        val docRef = db.collection("users").document(auth.currentUser!!.uid)
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val user = document.toObject<UserProfile>()
                binding.lblName.text = user?.name
                binding.lblEmail.text = user?.email
                binding.lblPhone.text = user?.phoneNumber
                GlideLoader(this.requireContext()).loadImage(user!!.profileUrl, binding.imgProfile)
            }
        }.addOnFailureListener { _ ->
            ThisDevice(this.requireContext()).showToast("Failed to retrieve profile details")
        }
    }
}