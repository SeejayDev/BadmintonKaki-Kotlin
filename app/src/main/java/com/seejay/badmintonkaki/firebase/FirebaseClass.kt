package com.seejay.badmintonkaki.firebase

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.seejay.badmintonkaki.R
import com.seejay.badmintonkaki.models.Account
import com.seejay.badmintonkaki.ui.fragments.*
import java.util.*

class FirebaseClass {
    private var storage = Firebase.storage
    private val storageRef = storage.reference

    fun uploadProfileImageToStorage(fragment: ProfileCreateFragment, uri: Uri, imgID: String){
        val imgRef = storageRef.child("images/$imgID")

        val uploadTask = imgRef.putFile(uri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imgRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                // Toast.makeText(fragment.context, "Image uploaded", Toast.LENGTH_SHORT).show()
                fragment.createAccount(downloadUri.toString())
            } else {
                Toast.makeText(fragment.context, "Image failed to upload", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun uploadUpdatedProfileImageToStorage(fragment: ProfileUpdateFragment, uri: Uri?, imgID: String){
        val imgRef = storageRef.child("images/$imgID")

        if (uri != null) {
            val uploadTask = imgRef.putFile(uri)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imgRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // delete the old image
                    val oldRef = storage.reference.child("images/${fragment.oldImageId}")
                    oldRef.delete()

                    // get the download url
                    val downloadUri = task.result
                    fragment.updateAccount(downloadUri.toString())
                } else {
                    Toast.makeText(fragment.context, "Image failed to upload", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            fragment.updateAccount(fragment.oldImageUrl)
        }
    }

    fun uploadGroupImageToStorage(fragment: GroupCreateFragment, uri: Uri, imgID: String){
        val imgRef = storageRef.child("images/$imgID")

        val uploadTask = imgRef.putFile(uri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imgRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                // Toast.makeText(fragment.context, "Image uploaded", Toast.LENGTH_SHORT).show()
                fragment.createGroup(downloadUri.toString())
            } else {
                Toast.makeText(fragment.context, "Image failed to upload", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun uploadUpdatedGroupImageToStorage(fragment: GroupUpdateFragment, uri: Uri?, imgID: String){
        val imgRef = storageRef.child("images/$imgID")

        if (uri != null) {
            val uploadTask = imgRef.putFile(uri)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imgRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // delete the old image
                    val oldRef = storage.getReferenceFromUrl(fragment.thisGroup.groupImage)
                    oldRef.delete()

                    // get the download url
                    val downloadUri = task.result
                    fragment.updateGroup(downloadUri.toString())
                } else {
                    Toast.makeText(fragment.context, "Image failed to upload", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            fragment.updateGroup(fragment.thisGroup.groupImage)
        }
    }

}