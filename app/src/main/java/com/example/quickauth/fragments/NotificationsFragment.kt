package com.example.quickauth.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quickauth.R
import com.example.quickauth.adapters.NotificationAdapter
import com.example.quickauth.databinding.FragmentNotificationsBinding
import com.example.quickauth.models.Notification

class NotificationsFragment : Fragment() {
    
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var notificationAdapter: NotificationAdapter
    private val notifications = mutableListOf<Notification>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupNotifications()
        setupRecyclerView()
        setupSwipeToDelete()
        setupClickListeners()
        updateNotificationBadge()
        updateUI()
    }
    
    override fun onResume() {
        super.onResume()
        // Update UI when returning to this fragment
        updateUI()
        updateNotificationBadge()
    }
    
    private fun setupClickListeners() {
        binding.btnRefresh.setOnClickListener {
            refreshNotifications()
        }
    }
    
    private fun refreshNotifications() {
        binding.btnRefresh.isEnabled = false
        binding.btnRefresh.text = "Refreshing..."
        
        // Rotate bell icon
        binding.ivNotificationsIcon.animate()
            .rotation(360f)
            .setDuration(1000)
            .withEndAction {
                // Only refresh if notifications are empty (don't recreate deleted ones)
                if (notifications.isEmpty()) {
                    setupNotifications()
                    notificationAdapter.notifyDataSetChanged()
                    updateUI()
                    updateNotificationBadge()
                } else {
                    Toast.makeText(context, "No new notifications", Toast.LENGTH_SHORT).show()
                }
                binding.btnRefresh.isEnabled = true
                binding.btnRefresh.text = "Refresh"
            }
            .start()
    }
    
    private fun setupNotifications() {
        // Only add notifications if list is empty (prevent recreation)
        if (notifications.isEmpty()) {
            notifications.addAll(listOf(
                Notification(
                    id = "1",
                    title = "Security Alert",
                    message = "Your account has been accessed from a new device",
                    time = "2 hours ago",
                    icon = R.drawable.ic_security
                ),
                Notification(
                    id = "2", 
                    title = "OTP Verification",
                    message = "Your OTP verification was successful",
                    time = "5 hours ago",
                    icon = R.drawable.ic_phone
                ),
                Notification(
                    id = "3",
                    title = "Welcome to QuickAuth",
                    message = "Your account has been successfully created",
                    time = "1 day ago",
                    icon = R.drawable.ic_security
                )
            ))
        }
    }
    
    private fun setupRecyclerView() {
        notificationAdapter = NotificationAdapter(notifications) { notification ->
            // Handle notification click
            Toast.makeText(context, "Notification clicked: ${notification.title}", Toast.LENGTH_SHORT).show()
        }
        
        binding.rvNotifications.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = notificationAdapter
        }
    }
    
    private fun setupSwipeToDelete() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false
            
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val notification = notifications[position]
                
                showDeleteConfirmationDialog(notification, position)
            }
        })
        
        itemTouchHelper.attachToRecyclerView(binding.rvNotifications)
    }
    
    private fun showDeleteConfirmationDialog(notification: Notification, position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Notification")
            .setMessage("Do you want to delete this notification?")
            .setPositiveButton("Yes") { _, _ ->
                deleteNotification(position)
            }
            .setNegativeButton("No") { _, _ ->
                // Restore the notification
                notificationAdapter.notifyItemChanged(position)
            }
            .setCancelable(false)
            .show()
    }
    
    private fun deleteNotification(position: Int) {
        notifications.removeAt(position)
        notificationAdapter.notifyItemRemoved(position)
        updateNotificationBadge()
        updateUI()
        Toast.makeText(context, "Notification deleted", Toast.LENGTH_SHORT).show()
    }
    
    private fun updateUI() {
        if (notifications.isEmpty()) {
            // Show empty state
            binding.ivNotificationsIcon.visibility = View.VISIBLE
            binding.tvNoNotifications.visibility = View.VISIBLE
            binding.tvNotificationsSubtitle.visibility = View.VISIBLE
            binding.btnRefresh.visibility = View.VISIBLE
            binding.rvNotifications.visibility = View.GONE
            
            // Update text to show count
            binding.tvNoNotifications.text = "0 Notifications"
        } else {
            // Show notifications list
            binding.ivNotificationsIcon.visibility = View.GONE
            binding.tvNoNotifications.visibility = View.GONE
            binding.tvNotificationsSubtitle.visibility = View.GONE
            binding.btnRefresh.visibility = View.GONE
            binding.rvNotifications.visibility = View.VISIBLE
        }
    }
    
    private fun updateNotificationBadge() {
        // Update badge count in parent activity
        (activity as? com.example.quickauth.DashboardActivity)?.updateNotificationBadge(notifications.size)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
