package com.seejay.badmintonkaki.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.seejay.badmintonkaki.R
import com.seejay.badmintonkaki.databinding.FragmentGroupUpdateBinding
import com.seejay.badmintonkaki.firebase.FirebaseClass
import com.seejay.badmintonkaki.models.Group
import com.seejay.badmintonkaki.utilities.GlideLoader
import com.seejay.badmintonkaki.utilities.ThisDevice
import java.util.*

class GroupUpdateFragment: Fragment() {
    private lateinit var binding: FragmentGroupUpdateBinding
    private lateinit var navController: NavController
    lateinit var thisGroup: Group
    val auth = Firebase.auth
    val db = Firebase.firestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentGroupUpdateBinding>(inflater,
            R.layout.fragment_group_update, container, false)

        // Firebase stuff
        navController = findNavController()

        // get group from args
        val args = GroupUpdateFragmentArgs.fromBundle(requireArguments())
        thisGroup = args.groupObject

        // load values into spinners
        binding.spinnerState.adapter = ArrayAdapter.createFromResource(
            this.requireContext(),
            R.array.states,
            R.layout.spinner_item)
            .also {it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)}

        binding.spinnerSkill.adapter = ArrayAdapter.createFromResource(
            this.requireContext(),
            R.array.skills,
            R.layout.spinner_item)
            .also {it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)}

        // listeners
        setListeners()

        // fill old values
        loadValues()

        return binding.root
    }

    fun setListeners() {
        binding.btnSelectImage.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            gallery.setType("image/*")
            startActivityForResult(gallery, 1)
        }
        
        binding.btnUpdateGroup.setOnClickListener {
            if (binding.txtGroupName.text.length < 1) {
                ThisDevice(this.requireContext()).showToast("Group name cannot be empty")
            } else {
                if (binding.txtCourtLocation.text.length < 1) {
                    ThisDevice(this.requireContext()).showToast("Court location cannot be empty")
                } else {
                    if (binding.spinnerState.selectedItemPosition == 0) {
                        ThisDevice(this.requireContext()).showToast("Select the group's state")
                    } else {
                        if (binding.spinnerSkill.selectedItemPosition == 0) {
                            ThisDevice(this.requireContext()).showToast("Select the group's skill level")
                        } else {
                            toggleButtonState()
                            uploadGroupPic()
                        }
                    }
                }
            }
        }
    }

    fun uploadGroupPic() {
        // get the URI of the image
        var file: Uri?
        if (binding.imgCover.tag != "") {
            file = Uri.parse(binding.imgCover.tag.toString())
        } else {
            file = null
        }

        // generate a random ID
        var uniqueId = UUID.randomUUID().toString()

        // run the upload
        FirebaseClass().uploadUpdatedGroupImageToStorage(this, file, uniqueId)
    }

    // function called once the image has been uploaded
    fun updateGroup(imageUrl: String) {
        val updatedGroup = Group(
            thisGroup.groupId,
            binding.txtGroupName.text.toString(),
            thisGroup.groupOwnerId,
            imageUrl,
            binding.txtCourtLocation.text.toString(),
            binding.spinnerState.selectedItem.toString(),
            binding.spinnerSkill.selectedItem.toString(),
            thisGroup.groupMembers
        )

        // create the group and store the generated ID
        db.collection("groups").document(thisGroup.groupId).set(updatedGroup).addOnSuccessListener { _ ->
            toggleButtonState()
            ThisDevice(this.requireContext()).showToast("Group updated")
            navController.navigate(GroupUpdateFragmentDirections.actionGroupUpdateFragmentToGroupPageAdminFragment(updatedGroup))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // listen for if the user selects an image
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            binding.imgCover.setImageURI(data?.data)
            binding.imgCover.setTag(data?.data.toString())
        }
    }

    fun loadValues() {
        binding.txtGroupName.setText(thisGroup.groupName)
        binding.txtCourtLocation.setText(thisGroup.groupLocation)

        var states = resources.getStringArray(R.array.states)
        var selectedState = states.indexOf(thisGroup.groupState)
        binding.spinnerState.setSelection(selectedState)

        var skills = resources.getStringArray(R.array.skills)
        var selectedSkill = skills.indexOf(thisGroup.groupSkill)
        binding.spinnerSkill.setSelection(selectedSkill)

        GlideLoader(this.requireContext()).loadImage(thisGroup.groupImage, binding.imgCover)
    }

    fun toggleButtonState() {
        binding.btnUpdateGroup.isEnabled = !binding.btnUpdateGroup.isEnabled
    }
}