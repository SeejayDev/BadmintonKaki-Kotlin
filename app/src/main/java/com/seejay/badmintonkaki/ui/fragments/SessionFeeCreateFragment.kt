package com.seejay.badmintonkaki.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.seejay.badmintonkaki.R
import com.seejay.badmintonkaki.databinding.FragmentSessionFeeCreateBinding

class SessionFeeCreateFragment: Fragment() {
    private lateinit var binding: FragmentSessionFeeCreateBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentSessionFeeCreateBinding>(inflater,
            R.layout.fragment_session_fee_create, container, false)

        return binding.root
    }
}