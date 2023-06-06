package com.seejay.badmintonkaki.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.seejay.badmintonkaki.R
import com.seejay.badmintonkaki.databinding.ActivityMainBinding
import com.seejay.badmintonkaki.utilities.ThisDevice


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var navButtons:LinearLayout
    private lateinit var btnDiscoverGroups: ImageButton
    private lateinit var btnMyGroups: ImageButton
    private lateinit var btnViewProfile: ImageButton
    private lateinit var auth: FirebaseAuth

    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        navController = findNavController(R.id.myNavHostFragment)
        supportActionBar?.hide()
        findViews()
        setListeners()
    }

    private fun findViews() {
        navButtons = binding.navigationButtons
        btnDiscoverGroups = binding.btnDiscover
        btnMyGroups = binding.btnGroups
        btnViewProfile = binding.btnProfile
    }

    private fun setListeners() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Toast.makeText(this, destination.label, Toast.LENGTH_SHORT).show()
            if (destination.id != R.id.loginFragment && destination.id != R.id.accountCreateFragment) {
                checkLoggedIn()
            }
            hideShowButtons(destination.id)
            changeButtonColors(destination.id)
        }

        btnDiscoverGroups.setOnClickListener {
            navController.popBackStack()
            navController.navigate(R.id.action_global_groupsDiscoverFragment)
            ThisDevice(this).vibrate()
        }
        btnMyGroups.setOnClickListener {
            navController.popBackStack()
            navController.navigate(R.id.action_global_myGroupsFragment)
            ThisDevice(this).vibrate()
        }
        btnViewProfile.setOnClickListener {
            navController.popBackStack()
            navController.navigate(R.id.action_global_profileFragment)
            ThisDevice(this).vibrate()
        }
    }

    private fun changeButtonColors(fragment: Int) {
        if (fragment == R.id.groupsDiscoverFragment) {
            btnDiscoverGroups.setImageResource(R.drawable.ic_search_blue)
            btnMyGroups.setImageResource(R.drawable.ic_group_black)
            btnViewProfile.setImageResource(R.drawable.ic_username_black)
        } else if (fragment == R.id.myGroupsFragment) {
            btnDiscoverGroups.setImageResource(R.drawable.ic_search_black)
            btnMyGroups.setImageResource(R.drawable.ic_group_blue)
            btnViewProfile.setImageResource(R.drawable.ic_username_black)
        } else if (fragment == R.id.profileFragment) {
            btnDiscoverGroups.setImageResource(R.drawable.ic_search_black)
            btnMyGroups.setImageResource(R.drawable.ic_group_black)
            btnViewProfile.setImageResource(R.drawable.ic_username_blue)
        }
    }

    private fun hideShowButtons(fragment: Int) {
        val noButtons = arrayOf(R.id.loginFragment, R.id.profileCreateFragment, R.id.accountCreateFragment, R.id.splashFragment, R.id.navigation)
        if (noButtons.contains(fragment)) {
            navButtons.visibility = View.GONE
        } else {
            navButtons.visibility = View.VISIBLE
        }
    }

    private fun checkLoggedIn() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Session expired, please log in again.", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
            navController.navigate(R.id.action_global_loginFragment)
        }
    }
}