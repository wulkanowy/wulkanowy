package io.github.wulkanowy.ui.modules.notificationscentre

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.data.db.entities.Notification
import io.github.wulkanowy.databinding.ItemNotificationsCentreBinding
import io.github.wulkanowy.utils.toFormattedString
import javax.inject.Inject

class NotificationsCentreAdapter @Inject constructor() :
    ListAdapter<Notification, NotificationsCentreAdapter.ViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemNotificationsCentreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        with(holder.binding) {
            notificationsCentreItemTitle.text = item.title
            notificationsCentreItemContent.text = item.content
            notificationsCentreItemDate.text = item.date.toFormattedString("HH:mm, d MMM")
        }
    }

    class ViewHolder(val binding: ItemNotificationsCentreBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class DiffUtilCallback : DiffUtil.ItemCallback<Notification>() {

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification) =
            oldItem == newItem

        override fun areItemsTheSame(oldItem: Notification, newItem: Notification) =
            oldItem.id == newItem.id
    }
}