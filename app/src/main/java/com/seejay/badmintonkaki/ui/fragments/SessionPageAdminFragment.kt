package com.seejay.badmintonkaki.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import com.seejay.badmintonkaki.adapters.MembersAdapter
import com.seejay.badmintonkaki.databinding.FragmentSessionPageAdminBinding
import com.seejay.badmintonkaki.models.Group
import com.seejay.badmintonkaki.models.Session
import com.seejay.badmintonkaki.models.UserProfile
import com.seejay.badmintonkaki.utilities.ThisDevice
import java.text.SimpleDateFormat
import java.util.*

class SessionPageAdminFragment: Fragment() {
    private lateinit var binding:FragmentSessionPageAdminBinding
    private lateinit var navController: NavController
    private lateinit var thisGroup: Group
    private lateinit var thisSession: Session
    val db = Firebase.firestore
    val auth = Firebase.auth
    var memberNotJoinedList = mutableListOf<UserProfile>()

    var adapter = MembersAdapter() {
        removeMember(it.profileId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentSessionPageAdminBinding>(inflater,
            R.layout.fragment_session_page_admin, container, false)

        // load objects
        val args = SessionPageAdminFragmentArgs.fromBundle(requireArguments())
        thisSession = args.sessionObject
        thisGroup = args.groupObject

        navController = findNavController()

        // listen for button click
        setListeners()

        binding.recyclerviewMembers.adapter = adapter
        loadPageData()

        return binding.root
    }

    private fun setListeners() {
        binding.btnJoinSession.setOnClickListener {
            ThisDevice(requireContext()).vibrate()
            addMember(auth.currentUser!!.uid)
            ThisDevice(this.requireContext()).showToast("Joined Session!")
        }

        binding.btnLeaveSession.setOnClickListener {
            ThisDevice(requireContext()).vibrate()
            removeMember(auth.currentUser!!.uid)
            ThisDevice(this.requireContext()).showToast("Left Session!")
        }

        binding.btnAddMember.setOnClickListener {
            if (binding.spinnerMembers.selectedItemPosition >= 0) {
                addMember(memberNotJoinedList[binding.spinnerMembers.selectedItemPosition].profileId)
            }
        }

        binding.spinnerMembers.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}
        }

        binding.btnFee.setOnClickListener {
            navController.navigate(R.id.action_sessionPageAdminFragment_to_sessionFeeCreateFragment)
        }
    }

    private fun loadPageData() {
        // static data population
        val sessionDateSplit = thisSession.sessionDate.split("/").toTypedArray()

        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, sessionDateSplit[0].toInt())
        cal.set(Calendar.MONTH, sessionDateSplit[1].toInt())
        cal.set(Calendar.YEAR, sessionDateSplit[2].toInt())
        var sessionDateString = SimpleDateFormat("d MMMM yyyy").format(cal.time)

        binding.lblSessionDate.setText(sessionDateString)
        binding.lblSessionTime.setText(thisSession.sessionTimeStart + " - " + thisSession.sessionTimeEnd)
        binding.lblSessionLocation.setText(thisGroup.groupLocation)
        binding.lblSessionDescription.setText(thisSession.sessionDescription)


        // display CTA buttons
        binding.btnJoinSession.visibility = View.GONE
        binding.btnLeaveSession.visibility = View.GONE
        if (thisSession.sessionMembers.contains(auth.currentUser!!.uid)) {
            binding.btnLeaveSession.visibility = View.VISIBLE
        } else {
            binding.btnJoinSession.visibility = View.VISIBLE
            if (thisSession.sessionMembers.size < thisSession.sessionLimit || thisSession.sessionLimit == 0) {
                binding.btnJoinSession.isEnabled = true
            } else {
                binding.btnJoinSession.isEnabled = false
            }
        }

        // display members
        var memberList = mutableListOf<UserProfile>()
        adapter.data = memberList
        for (memberId in thisSession.sessionMembers) {
            db.collection("users").document(memberId).get().addOnSuccessListener { document ->
                if (document != null) {
                    val user = document.toObject<UserProfile>()
                    memberList.add(user!!)

                    adapter.data = memberList
                }
            }
        }


        // load members in group but not in session into spinner
        memberNotJoinedList.clear()

        var membersInGroup = thisGroup.groupMembers
        var membersInSession = thisSession.sessionMembers
        var groupMembersNotInSession = mutableListOf<String>()
        for (gm in membersInGroup) {
            if (!membersInSession.contains(gm)) {
                groupMembersNotInSession.add(gm)
            }
        }

        binding.spinnerMembers.adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, memberNotJoinedList)
            .also {it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)}
        for (memberId in groupMembersNotInSession) {
            db.collection("users").document(memberId).get().addOnSuccessListener { document ->
                val user = document.toObject<UserProfile>()
                memberNotJoinedList.add(user!!)

                binding.spinnerMembers.adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, memberNotJoinedList)
                    .also {it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)}
            }
        }


        // show number of members in session
        if (thisSession.sessionLimit == 0) {
            binding.lblSessionLimit.setText("" + thisSession.sessionMembers.size + " members")
        } else {
            binding.lblSessionLimit.setText("" + thisSession.sessionMembers.size + "/" + thisSession.sessionLimit)
        }
    }

    private fun addMember(toAdd: String) {
        var memberList = thisSession.sessionMembers.toMutableList()
        memberList.add(toAdd)
        thisSession.sessionMembers = memberList.toList()

        db.collection("sessions").document(thisSession.sessionId).set(thisSession)
        loadPageData()
    }

    private fun removeMember(toRemove: String) {
        var memberList = thisSession.sessionMembers.toMutableList()
        memberList.removeAt(memberList.indexOf(toRemove))
        thisSession.sessionMembers = memberList.toList()

        db.collection("sessions").document(thisSession.sessionId).set(thisSession)
        loadPageData()
    }
}