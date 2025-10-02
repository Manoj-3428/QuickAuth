package com.example.quickauth.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quickauth.R
import com.example.quickauth.models.Notification

class NotificationAdapter(
    private val notifications: List<Notification>,
    private val onNotificationClick: (Notification) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIcon: ImageView = itemView.findViewById(R.id.iv_notification_icon)
        val tvTitle: TextView = itemView.findViewById(R.id.tv_notification_title)
        val tvMessage: TextView = itemView.findViewById(R.id.tv_notification_message)
        val tvTime: TextView = itemView.findViewById(R.id.tv_notification_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        
        holder.ivIcon.setImageResource(notification.icon)
        holder.tvTitle.text = notification.title
        holder.tvMessage.text = notification.message
        holder.tvTime.text = notification.time
        
        holder.itemView.setOnClickListener {
            onNotificationClick(notification)
        }
    }

    override fun getItemCount(): Int = notifications.size
}
