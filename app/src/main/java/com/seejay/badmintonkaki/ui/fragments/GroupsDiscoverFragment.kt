package com.seejay.badmintonkaki.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.seejay.badmintonkaki.R
import com.seejay.badmintonkaki.adapters.GroupsAdapter
import com.seejay.badmintonkaki.databinding.FragmentGroupsDiscoverBinding
import com.seejay.badmintonkaki.models.Group
import com.seejay.badmintonkaki.utilities.ThisDevice

class GroupsDiscoverFragment: Fragment() {
    private lateinit var binding: FragmentGroupsDiscoverBinding
    private lateinit var navController: NavController
    val db = Firebase.firestore
    var spinnersLoaded = 0

    var adapter = GroupsAdapter() {
        navigateToGroupPage(it)
    }

    override fun onStart() {
        super.onStart()
        spinnersLoaded = 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentGroupsDiscoverBinding>(inflater,
            R.layout.fragment_groups_discover, container, false)

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

        navController = findNavController()
        setListeners()

        binding.recyclerviewGroups.adapter = adapter
        fetchData()

        return binding.root
    }

    private fun setListeners() {
        binding.refreshGroups.setOnRefreshListener {
            fetchData()
        }

        binding.spinnerSkill.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (spinnersLoaded > 1) {
                    fetchData()
                }
                // prevent initial click
                spinnersLoaded++
            }
        }

        binding.spinnerState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (spinnersLoaded > 1) {
                    fetchData()
                }
                // prevent initial click
                spinnersLoaded++
            }
        }
    }

    private fun fetchData() {
        var data = mutableListOf<Group>()

        var groupsRef = db.collection("groups")

        var groupsQuery: Query = groupsRef

        if (binding.spinnerSkill.selectedItemPosition != 0) {
            groupsQuery = groupsQuery.whereEqualTo("groupSkill", binding.spinnerSkill.selectedItem.toString())
        }

        if (binding.spinnerState.selectedItemPosition != 0) {
            groupsQuery = groupsQuery.whereEqualTo("groupState", binding.spinnerState.selectedItem.toString())
        }

        groupsQuery
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
        navController.navigate(GroupsDiscoverFragmentDirections.actionGroupsDiscoverFragmentToGroupPageAdminFragment(group))
    }
}