package com.FanMessenGer.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.FanMessenGer.data.model.Message
import com.FanMessenGer.databinding.ItemMessageBinding
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(
    private val onMessageClick: (Message) -> Unit
) : ListAdapter<Message, MessageAdapter.ViewHolder>(MessageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onMessageClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemMessageBinding,
        private val onMessageClick: (Message) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.apply {
                tvMessage.text = message.body
                tvTimestamp.text = formatTime(message.timestamp)

                // Show status indicators
                tvStatus.text = when {
                    message.isSpam -> "🚫 SPAM"
                    message.isBlocked -> "🚷 BLOCKED"
                    message.isFailed -> "❌ FAILED"
                    message.isDelivered -> "✓✓ DELIVERED"
                    message.isSent -> "✓ SENT"
                    else -> ""
                }

                root.setOnClickListener {
                    onMessageClick(message)
                }
            }
        }

        private fun formatTime(timestamp: Long): String {
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }
}

class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Message, newItem: Message) =
        oldItem == newItem
}
