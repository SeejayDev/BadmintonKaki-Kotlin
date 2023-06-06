package com.seejay.badmintonkaki.ui.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import com.google.firebase.ktx.Firebase
import com.seejay.badmintonkaki.R
import com.seejay.badmintonkaki.databinding.FragmentSessionCreateBinding
import com.seejay.badmintonkaki.models.Group
import com.seejay.badmintonkaki.models.Session
import com.seejay.badmintonkaki.utilities.ThisDevice
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.datatype.DatatypeConstants.MONTHS

class SessionCreateFragment: Fragment() {
    private lateinit var binding: FragmentSessionCreateBinding
    private lateinit var thisGroup: Group
    private lateinit var navController: NavController
    val db = Firebase.firestore
    val auth = Firebase.auth

    // date time
    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = 0
    private var minute = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentSessionCreateBinding>(inflater,
            R.layout.fragment_session_create, container, false)

        // get group object
        // load group object
        val args = SessionCreateFragmentArgs.fromBundle(requireArguments())
        thisGroup = args.groupObject

        // load navController
        navController = findNavController()

        // listen for clicks
        setListeners()

        return binding.root
    }

    private fun setListeners() {
        binding.btnSelectDate.setOnClickListener {
            getDateTimeCalendar()
            val chosenListener =  DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val cal = Calendar.getInstance()
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.YEAR, year)
                binding.lblDate.visibility = View.VISIBLE
                binding.lblDate.setText(SimpleDateFormat("dd/MM/yyyy").format(cal.time))
            }

            DatePickerDialog(requireContext(), chosenListener, year, month, day).show()
        }

        binding.btnSelectStartTime.setOnClickListener {
            getDateTimeCalendar()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                binding.lblStartTime.visibility = View.VISIBLE
                binding.lblStartTime.setText(SimpleDateFormat("HH:mm").format(cal.time))
            }

            TimePickerDialog(requireContext(), timeSetListener, hour, minute, false).show()
        }

        binding.btnSelectEndTime.setOnClickListener {
            getDateTimeCalendar()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                binding.lblEndTime.visibility = View.VISIBLE
                binding.lblEndTime.setText(SimpleDateFormat("HH:mm").format(cal.time))
            }

            TimePickerDialog(requireContext(), timeSetListener, hour, minute, false).show()
        }

        binding.btnCreateSession.setOnClickListener {
            if (binding.lblDate.text.toString() == "") {
                ThisDevice(requireContext()).showToast("Select a date")
            } else {
                if (binding.lblStartTime.text.toString() == "") {
                    ThisDevice(requireContext()).showToast("Select a starting time")
                } else {
                    if (binding.lblEndTime.text.toString() == "") {
                        ThisDevice(requireContext()).showToast("Select an ending time")
                    } else {
                        if (!validateTime()) {
                            ThisDevice(requireContext()).showToast("Invalid time range")
                        } else {
                            if (binding.txtDescription.text.toString() == "") {
                                ThisDevice(requireContext()).showToast("Description required")
                            } else {
                                toggleButtonState()
                                createSession()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getDateTimeCalendar(){
        val calendar = Calendar.getInstance()
        day     = calendar.get(Calendar.DAY_OF_MONTH)
        month   = calendar.get(Calendar.MONTH)
        year    = calendar.get(Calendar.YEAR)
        hour    = calendar.get(Calendar.HOUR_OF_DAY)
        minute  = calendar.get(Calendar.MINUTE)
    }

    private fun validateTime():Boolean {
        val startTime = binding.lblStartTime.text.toString().split(":").toTypedArray()
        val endTime = binding.lblEndTime.text.toString().split(":").toTypedArray()

        if (startTime[0].toInt() > endTime[0].toInt()) {
            return false
        } else if (startTime[0].toInt() == endTime[0].toInt()) {
            if (startTime[1].toInt() >= endTime[1].toInt()) {
                return false
            }
        }
        return true
    }

    fun toggleButtonState() {
        binding.btnCreateSession.isEnabled = !binding.btnCreateSession.isEnabled
    }

    private fun createSession() {
        var newSession = Session(
            "",
            thisGroup.groupId,
            binding.lblDate.text.toString(),
            binding.lblStartTime.text.toString(),
            binding.lblEndTime.text.toString(),
            binding.txtDescription.text.toString(),
            binding.txtLimit.text.toString().toInt(),
            listOf()
        )

        db.collection("sessions").add(newSession).addOnSuccessListener { docRef ->
            docRef.update("sessionId", docRef.id)

            toggleButtonState()
            ThisDevice(this.requireContext()).showToast("Session created")
            navController.navigate(SessionCreateFragmentDirections.actionSessionCreateFragmentToGroupPageAdminFragment(thisGroup))
        }
    }
}