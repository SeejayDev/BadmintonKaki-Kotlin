package com.seejay.badmintonkaki.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.seejay.badmintonkaki.R
import com.seejay.badmintonkaki.adapters.GroupsAdapter
import com.seejay.badmintonkaki.databinding.FragmentMyGroupsBinding
import com.seejay.badmintonkaki.models.Group
import com.seejay.badmintonkaki.utilities.ThisDevice

class MyGroupsFragment: Fragment() {
    private lateinit var binding:FragmentMyGroupsBinding
    private lateinit var navController: NavController
    var adapter = GroupsAdapter() {
        navigateToGroupPage(it)
    }
    val db = Firebase.firestore
    var auth = Firebase.auth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentMyGroupsBinding>(inflater,
            R.layout.fragment_my_groups, container, false)

        navController = findNavController()
        setListeners()

        binding.recyclerviewGroups.adapter = adapter
        fetchData()

        return binding.root
    }

    fun setListeners() {
        binding.btnCreate.setOnClickListener {
            navController.navigate(R.id.action_myGroupsFragment_to_groupCreateFragment)
        }

        binding.refreshGroups.setOnRefreshListener {
            fetchData()
        }
    }

    private fun fetchData() {
        var data = mutableListOf<Group>()

        db.collection("groups")
            .whereArrayContains("groupMembers", auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { result->
                for (document in result) {
                    var newGroup = document.toObject<Group>()
                    data.add(newGroup)
                }

                adapter.data = data

                if (data.size == 0) {
                    ThisDevice(this.requireContext()).showToast("No groups found!")
                } else {
                    // ThisDevice(this.requireContext()).showToast("List loaded!")
                }
                binding.refreshGroups.isRefreshing = false
            }
    }

    private fun navigateToGroupPage(group: Group) {
        // ThisDevice(this.requireContext()).showToast(group.groupId)
        ThisDevice(this.requireContext()).vibrate()
        navController.navigate(MyGroupsFragmentDirections.actionMyGroupsFragmentToGroupPageAdminFragment(group))
    }
}