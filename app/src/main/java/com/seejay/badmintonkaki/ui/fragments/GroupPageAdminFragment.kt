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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.seejay.badmintonkaki.R
import com.seejay.badmintonkaki.adapters.SessionsAdapter
import com.seejay.badmintonkaki.databinding.FragmentGroupPageAdminBinding
import com.seejay.badmintonkaki.models.Group
import com.seejay.badmintonkaki.models.Session
import com.seejay.badmintonkaki.models.UserProfile
import com.seejay.badmintonkaki.utilities.GlideLoader
import com.seejay.badmintonkaki.utilities.ThisDevice

class GroupPageAdminFragment: Fragment() {
    private lateinit var binding: FragmentGroupPageAdminBinding
    private lateinit var thisGroup: Group
    private lateinit var navController:NavController
    val db = Firebase.firestore
    val auth = Firebase.auth

    var adapter = SessionsAdapter() {
        navigateToSessionPage(it)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentGroupPageAdminBinding>(inflater,
            R.layout.fragment_group_page_admin, container, false)

        navController = findNavController()

        // load group object
        val args = GroupPageAdminFragmentArgs.fromBundle(requireArguments())
        thisGroup = args.groupObject

        // load group values
        loadPageData()

        // set functions
        setListeners()

        binding.recyclerviewSessions.adapter = adapter
        fetchData()

        return binding.root
    }

    private fun setListeners() {
        binding.btnBack.setOnClickListener {
            ThisDevice(requireContext()).vibrate()
            navController.popBackStack()
        }

        binding.refreshSessions.setOnRefreshListener {
            fetchData()
        }

        binding.btnJoin.setOnClickListener {
            ThisDevice(requireContext()).vibrate()
            var memberList = thisGroup.groupMembers.toMutableList()
            memberList.add(auth.currentUser!!.uid)
            thisGroup.groupMembers = memberList.toList()

            db.collection("groups").document(thisGroup.groupId).set(thisGroup)
            ThisDevice(this.requireContext()).showToast("Joined Group!")
            loadPageData()
        }

        binding.btnLeave.setOnClickListener {
            ThisDevice(requireContext()).vibrate()
            var memberList = thisGroup.groupMembers.toMutableList()
            memberList.removeAt(memberList.indexOf(auth.currentUser!!.uid))
            thisGroup.groupMembers = memberList.toList()

            db.collection("groups").document(thisGroup.groupId).set(thisGroup)
            ThisDevice(this.requireContext()).showToast("Left Group!")
            loadPageData()
        }

        binding.btnManage.setOnClickListener {
            ThisDevice(requireContext()).vibrate()
            navController.navigate(GroupPageAdminFragmentDirections.actionGroupPageAdminFragmentToGroupUpdateFragment(thisGroup))
        }

        binding.btnNewSession.setOnClickListener {
            ThisDevice(requireContext()).vibrate()
            navController.navigate(GroupPageAdminFragmentDirections.actionGroupPageAdminFragmentToSessionCreateFragment(thisGroup))
        }
    }

    private fun loadPageData() {
        GlideLoader(this.requireContext()).loadImage(thisGroup.groupImage, binding.imgBanner)
        binding.lblGroupName.setText(thisGroup.groupName)
        binding.lblGroupLocation.setText(thisGroup.groupLocation)
        binding.lblGroupSkill.setText(thisGroup.groupSkill)
        binding.lblGroupSize.setText(thisGroup.groupMembers.size.toString())

        db.collection("users").document(thisGroup.groupOwnerId).get().addOnSuccessListener { document ->
            val leader = document.toObject<UserProfile>()
            binding.lblGroupLeader.setText(leader?.name)
        }

        // check if user is a member
        binding.btnManage.visibility = View.GONE
        binding.btnJoin.visibility = View.GONE
        binding.btnLeave.visibility = View.GONE
        binding.newSession.visibility = View.GONE
        if (thisGroup.groupMembers.contains(auth.currentUser?.uid)) {
            if (thisGroup.groupOwnerId == auth.currentUser?.uid) {
                binding.btnManage.visibility = View.VISIBLE
                binding.newSession.visibility = View.VISIBLE
            } else {
                binding.btnLeave.visibility = View.VISIBLE
            }
        } else {
            // if not a member
            binding.btnJoin.visibility = View.VISIBLE
        }
    }

    private fun fetchData() {
        var data = mutableListOf<Session>()

        db.collection("sessions")
            .whereEqualTo("groupId", thisGroup.groupId)
            .get()
            .addOnSuccessListener { result->
                for (document in result) {
                    var newSession = document.toObject<Session>()
                    data.add(newSession)
                }

                adapter.data = data

                if (data.size == 0) {
                    ThisDevice(this.requireContext()).showToast("No sessions found!")
                } else {
                    // ThisDevice(this.requireContext()).showToast("List loaded!")
                }
                binding.refreshSessions.isRefreshing = false
            }
    }

    private fun navigateToSessionPage(session: Session) {
        ThisDevice(this.requireContext()).vibrate()

        // check if user is admin
        if (auth.currentUser!!.uid == thisGroup.groupOwnerId) {
            navController.navigate(GroupPageAdminFragmentDirections.actionGroupPageAdminFragmentToSessionPageAdminFragment(session, thisGroup))
        } else {
            navController.navigate(GroupPageAdminFragmentDirections.actionGroupPageAdminFragmentToSessionPageUserFragment(session, thisGroup))
        }
    }
}