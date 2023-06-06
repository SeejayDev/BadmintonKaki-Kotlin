package com.seejay.badmintonkaki.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.seejay.badmintonkaki.R
import com.seejay.badmintonkaki.models.Group
import com.seejay.badmintonkaki.models.Session
import com.seejay.badmintonkaki.utilities.GlideLoader
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class SessionsAdapter(val onItemClicked: (Session) -> Unit): RecyclerView.Adapter<SessionsAdapter.ViewHolder> () {
    var data = listOf<Session>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder(itemView: View, onItemClicked: (Int) -> Unit) : RecyclerView.ViewHolder(itemView){
        val sessionDate:TextView = itemView.findViewById(R.id.lbl_session_date)
        val sessionTime:TextView = itemView.findViewById(R.id.lbl_session_time)
        val btnViewSession: Button = itemView.findViewById(R.id.btn_view_session)
        val context = itemView.context

        init {
            btnViewSession.setOnClickListener {
                onItemClicked(adapterPosition)
            }
        }

        fun bind(item:Session) {
            val sessionDateSplit = item.sessionDate.split("/").toTypedArray()

            val cal = Calendar.getInstance()
            cal.set(Calendar.DAY_OF_MONTH, sessionDateSplit[0].toInt())
            cal.set(Calendar.MONTH, sessionDateSplit[1].toInt())
            cal.set(Calendar.YEAR, sessionDateSplit[2].toInt())
            var sessionDateString = SimpleDateFormat("d MMMM yyyy (EEEE)").format(cal.time)

            sessionDate.setText(sessionDateString)
            sessionTime.setText(item.sessionTimeStart + " - " + item.sessionTimeEnd)
        }
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.card_session, parent, false)
        return ViewHolder(view) {
            onItemClicked(data[it])
        }
    }
}