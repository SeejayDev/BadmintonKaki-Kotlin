package com.seejay.badmintonkaki.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.seejay.badmintonkaki.R
import com.seejay.badmintonkaki.databinding.FragmentProfileUpdateBinding
import com.seejay.badmintonkaki.firebase.FirebaseClass
import com.seejay.badmintonkaki.models.UserProfile
import com.seejay.badmintonkaki.utilities.GlideLoader
import com.seejay.badmintonkaki.utilities.ThisDevice
import java.util.*

class ProfileUpdateFragment: Fragment() {
    private lateinit var binding: FragmentProfileUpdateBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private val storage = Firebase.storage
    var oldImageId: String = ""
    var oldImageUrl: String = ""
    val db = Firebase.firestore

    var changed = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentProfileUpdateBinding>(inflater,
            R.layout.fragment_profile_update, container, false)

        navController = findNavController()
        auth = Firebase.auth
        setListeners()
        loadOldValues()

        return binding.root
    }

    fun setListeners() {
        binding.btnSelectImage.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            gallery.setType("image/*")
            startActivityForResult(gallery, 1)
        }

        binding.btnUpdate.setOnClickListener {
            if (binding.txtName.text.length < 1) {
                showToast("Name cannot be empty")
            } else {
                if (binding.txtPhoneNumber.text.length < 1) {
                    showToast("Phone number cannot be empty")
                } else {
                    if (binding.imgProfile.tag == "") {
                        showToast("Select a profile image")
                    } else {
                        toggleButtonState()
                        uploadProfilePic()
                    }
                }
            }
        }
    }

    fun loadOldValues() {
        val docRef = db.collection("users").document(auth.currentUser?.uid.toString())
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val user = document.toObject<UserProfile>()
                binding.txtName.setText(user?.name)
                binding.txtPhoneNumber.setText(user?.phoneNumber)

                val oldRef = storage.getReferenceFromUrl(user!!.profileUrl)
                binding.imgProfile.setTag(oldRef.name)

                GlideLoader(this.requireContext()).loadImage(user.profileUrl, binding.imgProfile)

                // saves the ID of the old image
                oldImageUrl = user.profileUrl
                oldImageId = oldRef.name
            }
        }.addOnFailureListener { _ ->
            ThisDevice(this.requireContext()).showToast("Failed to retrieve profile details")
        }
    }

    fun uploadProfilePic() {
        var file : Uri?
        // get the URI of the image
        if (changed) {
            file = Uri.parse(binding.imgProfile.tag.toString())
        } else {
            file = null
        }

        // generate a random ID
        var uniqueId = UUID.randomUUID().toString()

        // run the upload
        FirebaseClass().uploadUpdatedProfileImageToStorage(this, file, uniqueId)
    }

    // function called once the image has been uploaded
    fun updateAccount(imageUrl: String) {
        // save the new information into an object
        val updatedAccount = UserProfile(
            auth.currentUser!!.uid,
            binding.txtName.text.toString(),
            binding.txtPhoneNumber.text.toString(),
            auth.currentUser!!.email!!,
            imageUrl
        )

        db.collection("users").document(auth.currentUser!!.uid).set(updatedAccount)

        toggleButtonState()
        showToast("User Profile updated")
        navController.navigate(R.id.action_profileUpdateFragment_to_profileFragment)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // listen for if the user selects an image
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            binding.imgProfile.visibility = View.VISIBLE
            binding.imgProfile.setImageURI(data?.data)
            binding.imgProfile.setTag(data?.data.toString())
            changed = true
        }
    }

    fun showToast(error: String) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    }

    fun toggleButtonState() {
        binding.btnUpdate.isEnabled = !binding.btnUpdate.isEnabled
    }
}