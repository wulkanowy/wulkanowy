package io.github.wulkanowy.ui.modules.message.mailboxchooser

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.data.db.entities.Mailbox
import io.github.wulkanowy.databinding.ItemMailboxChooserBinding
import javax.inject.Inject

class MailboxChooserAdapter @Inject constructor(
) : ListAdapter<Mailbox, MailboxChooserAdapter.ItemViewHolder>(differ) {

    lateinit var onClickListener: (Mailbox) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemMailboxChooserBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ItemViewHolder(
        private val binding: ItemMailboxChooserBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Mailbox) {
            with(binding) {
                mailboxItemName.text = buildString {
                    if (item.studentName.isNotBlank() && item.studentName != item.userName) {
                        append(item.studentName)
                        append(" - ")
                    }
                    append(item.userName)
                }
                mailboxItemSchool.text = item.schoolNameShort

                root.setOnClickListener { onClickListener(item) }
            }
        }
    }

    companion object {
        private val differ = object : ItemCallback<Mailbox>() {
            override fun areItemsTheSame(oldItem: Mailbox, newItem: Mailbox): Boolean {
                return oldItem.globalKey == newItem.globalKey
            }

            override fun areContentsTheSame(oldItem: Mailbox, newItem: Mailbox): Boolean {
                return oldItem == newItem
            }
        }
    }
}
