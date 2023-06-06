package com.seejay.badmintonkaki.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.seejay.badmintonkaki.R
import com.seejay.badmintonkaki.models.Group
import com.seejay.badmintonkaki.utilities.GlideLoader

class GroupsAdapter(val onItemClicked: (Group) -> Unit): RecyclerView.Adapter<GroupsAdapter.ViewHolder> () {
    class ViewHolder(itemView: View, onItemClicked: (Int) -> Unit) : RecyclerView.ViewHolder(itemView){
        val bannerImage:ImageView = itemView.findViewById(R.id.img_banner)
        val groupName:TextView = itemView.findViewById(R.id.lbl_name)
        val groupLocation:TextView = itemView.findViewById(R.id.lbl_location)
        val groupSkill:TextView = itemView.findViewById(R.id.lbl_skill)
        val groupMembersCount:TextView = itemView.findViewById(R.id.lbl_members)
        val groupState:TextView = itemView.findViewById(R.id.lbl_state)
        val context = itemView.context

        val card: MaterialCardView = itemView.findViewById(R.id.group_card)

        init {
            card.setOnClickListener {
                onItemClicked(adapterPosition)
            }
        }

        fun bind(item:Group) {
            GlideLoader(context).loadImage(item.groupImage, bannerImage)
            groupName.setText(item.groupName)
            groupLocation.setText(item.groupLocation)
            groupSkill.setText(item.groupSkill)
            groupMembersCount.setText(item.groupMembers.size.toString())
            groupState.setText(item.groupState)
        }
    }

    var data = listOf<Group>()
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
        val view = layoutInflater.inflate(R.layout.card_group, parent, false)
        return ViewHolder(view) {
            onItemClicked(data[it])
        }
    }
}