package com.seejay.badmintonkaki.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.seejay.badmintonkaki.R
import com.seejay.badmintonkaki.databinding.FragmentGroupPageUserBinding

class GroupPageUserFragment: Fragment() {
    private lateinit var binding: FragmentGroupPageUserBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentGroupPageUserBinding>(inflater,
            R.layout.fragment_group_page_user, container, false)

        // this fragment is not used, it has been combined with the GroupPageAdminFragment

        return binding.root
    }
}