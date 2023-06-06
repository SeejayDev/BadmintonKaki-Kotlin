package com.seejay.badmintonkaki.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.seejay.badmintonkaki.R
import com.seejay.badmintonkaki.databinding.FragmentSessionPageUserBinding
import com.seejay.badmintonkaki.models.Group
import com.seejay.badmintonkaki.models.Session
import com.seejay.badmintonkaki.models.UserProfile
import com.seejay.badmintonkaki.utilities.ThisDevice
import java.text.SimpleDateFormat
import java.util.*

class SessionPageUserFragment: Fragment() {
    private lateinit var binding:FragmentSessionPageUserBinding
    private lateinit var navController: NavController
    private lateinit var thisGroup: Group
    private lateinit var thisSession: Session
    val db = Firebase.firestore
    val auth = Firebase.auth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentSessionPageUserBinding>(inflater,
            R.layout.fragment_session_page_user, container, false)

        // load objects
        val args = SessionPageUserFragmentArgs.fromBundle(requireArguments())
        thisSession = args.sessionObject
        thisGroup = args.groupObject

        // display information
        loadPageData()

        // listen for button click
        setListeners()

        return binding.root
    }

    private fun setListeners() {
        binding.btnJoinSession.setOnClickListener {
            ThisDevice(requireContext()).vibrate()
            var memberList = thisSession.sessionMembers.toMutableList()
            memberList.add(auth.currentUser!!.uid)
            thisSession.sessionMembers = memberList.toList()

            db.collection("sessions").document(thisSession.sessionId).set(thisSession)
            ThisDevice(this.requireContext()).showToast("Joined Session!")
            loadPageData()
        }

        binding.btnLeaveSession.setOnClickListener {
            ThisDevice(requireContext()).vibrate()
            var memberList = thisSession.sessionMembers.toMutableList()
            memberList.removeAt(memberList.indexOf(auth.currentUser!!.uid))
            thisSession.sessionMembers = memberList.toList()

            db.collection("sessions").document(thisSession.sessionId).set(thisSession)
            ThisDevice(this.requireContext()).showToast("Left Session!")
            loadPageData()
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
        if (thisGroup.groupMembers.contains(auth.currentUser!!.uid)) {
            if (thisSession.sessionMembers.contains(auth.currentUser!!.uid)) {
                binding.btnLeaveSession.visibility = View.VISIBLE
            } else {
                if (thisSession.sessionMembers.size < thisSession.sessionLimit || thisSession.sessionLimit == 0) {
                    binding.btnJoinSession.visibility = View.VISIBLE
                }
            }
        }

        // display members
        var memberList = mutableListOf<String>()
        binding.listSessions.adapter = ArrayAdapter(requireContext(), R.layout.member_list_item, memberList)
        for (memberId in thisSession.sessionMembers) {
            db.collection("users").document(memberId).get().addOnSuccessListener { document ->
                if (document != null) {
                    val user = document.toObject<UserProfile>()
                    memberList.add(user!!.name)

                    binding.listSessions.adapter = ArrayAdapter(requireContext(), R.layout.member_list_item, memberList)
                }
            }
        }

        if (thisSession.sessionLimit == 0) {
            binding.lblSessionLimit.setText("" + thisSession.sessionMembers.size + " members")
        } else {
            binding.lblSessionLimit.setText("" + thisSession.sessionMembers.size + "/" + thisSession.sessionLimit)
        }
    }
}