package com.seejay.badmintonkaki.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.google.firebase.ktx.Firebase
import com.seejay.badmintonkaki.R
import com.seejay.badmintonkaki.databinding.FragmentProfileCreateBinding
import com.seejay.badmintonkaki.firebase.FirebaseClass
import com.seejay.badmintonkaki.models.UserProfile
import com.seejay.badmintonkaki.utilities.ThisDevice
import java.util.*

class ProfileCreateFragment: Fragment() {
    private lateinit var binding: FragmentProfileCreateBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentProfileCreateBinding>(inflater,
            R.layout.fragment_profile_create, container, false)

        navController = findNavController()
        auth = Firebase.auth
        setListeners()

        return binding.root
    }

    fun setListeners() {
        binding.btnSelectImage.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            gallery.setType("image/*")
            startActivityForResult(gallery, 1)
        }

        binding.btnRegister.setOnClickListener {
            if (binding.txtName.text.length < 1) {
                ThisDevice(this.requireContext()).showToast("Name cannot be empty")
            } else {
                if (binding.txtPhoneNumber.text.length < 1) {
                    ThisDevice(this.requireContext()).showToast("Phone number cannot be empty")
                } else {
                    if (binding.imgProfile.tag == "") {
                        ThisDevice(this.requireContext()).showToast("Select a profile image")
                    } else {
                        toggleButtonState()
                        uploadProfilePic()
                    }
                }
            }
        }
    }

    fun uploadProfilePic() {
        // get the URI of the image
        var file = Uri.parse(binding.imgProfile.tag.toString())

        // generate a random ID
        var uniqueId = UUID.randomUUID().toString()

        // run the upload
        FirebaseClass().uploadProfileImageToStorage(this, file, uniqueId)
    }

    // function called once the image has been uploaded
    fun createAccount(imageUrl: String) {
        val newAccount = UserProfile(
            auth.currentUser!!.uid,
            binding.txtName.text.toString(),
            binding.txtPhoneNumber.text.toString(),
            auth.currentUser!!.email!!,
            imageUrl
        )

        db.collection("users").document(auth.currentUser!!.uid).set(newAccount)

        toggleButtonState()
        ThisDevice(this.requireContext()).showToast("User Profile created")
        navController.navigate(R.id.action_profileCreateFragment_to_loginFragment)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // listen for if the user selects an image
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            binding.imgProfile.visibility = View.VISIBLE
            binding.imgProfile.setImageURI(data?.data)
            binding.imgProfile.setTag(data?.data.toString())
        }
    }

    fun toggleButtonState() {
        binding.btnRegister.isEnabled = !binding.btnRegister.isEnabled
    }
}