package com.seejay.badmintonkaki.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.auth.User
import com.seejay.badmintonkaki.R
import com.seejay.badmintonkaki.models.Group
import com.seejay.badmintonkaki.models.UserProfile
import com.seejay.badmintonkaki.utilities.GlideLoader

class MembersAdapter(val onItemClicked: (UserProfile) -> Unit): RecyclerView.Adapter<MembersAdapter.ViewHolder> () {
    class ViewHolder(itemView: View, onItemClicked: (Int) -> Unit) : RecyclerView.ViewHolder(itemView){
        val memberName:TextView = itemView.findViewById(R.id.lbl_name)
        val removeButton:ImageButton = itemView.findViewById(R.id.btn_remove)
        val context = itemView.context

        init {
            removeButton.setOnClickListener {
                onItemClicked(adapterPosition)
            }
        }

        fun bind(item:UserProfile) {
            memberName.setText(item.name)
        }
    }

    var data = listOf<UserProfile>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.card_member, parent, false)
        return ViewHolder(view) {
            onItemClicked(data[it])
        }
    }
}