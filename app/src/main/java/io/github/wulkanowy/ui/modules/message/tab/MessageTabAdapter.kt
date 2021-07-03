package io.github.wulkanowy.ui.modules.message.tab

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.enums.MessageFolder
import io.github.wulkanowy.databinding.ItemMessageBinding
import io.github.wulkanowy.databinding.ItemMessageChipsBinding
import io.github.wulkanowy.utils.toFormattedString
import javax.inject.Inject

class MessageTabAdapter @Inject constructor() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_VIEW_TYPE_HEADER = 0
    private val ITEM_VIEW_TYPE_ITEM = 1

    var onItemClickListener: (Message, position: Int) -> Unit = { _, _ -> }
    var onHeaderClickListener: (chip: CompoundButton, isChecked: Boolean) -> Unit = { _, _ -> }

    var onChangesDetectedListener = {}

    private var items = mutableListOf<DataItem>()
    private var onlyUnread: Boolean? = null
    private var onlyWithAttachments = false

    fun setDataItems(data: List<Message>, _onlyUnread: Boolean?, _onlyWithAttachments: Boolean) {
        if (items.size != data.size) onChangesDetectedListener()
        val newItems = listOf(DataItem.Header) + data.map { DataItem.MessageItem(it) }
        val diffResult = DiffUtil.calculateDiff(MessageTabDiffUtil(items, newItems))
        items = newItems.toMutableList()
        onlyUnread = _onlyUnread
        onlyWithAttachments = _onlyWithAttachments
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> ITEM_VIEW_TYPE_HEADER
            else -> ITEM_VIEW_TYPE_ITEM
        }
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_VIEW_TYPE_ITEM -> ItemViewHolder(
                ItemMessageBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder(
                ItemMessageChipsBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemViewHolder -> {
                val item = (items[position] as DataItem.MessageItem).message

                with(holder.binding) {
                    val style = if (item.unread) Typeface.BOLD else Typeface.NORMAL

                    messageItemAuthor.run {
                        text =
                            if (item.folderId == MessageFolder.SENT.id) item.recipient else item.sender
                        setTypeface(null, style)
                    }
                    messageItemSubject.run {
                        text =
                            if (item.subject.isNotBlank()) item.subject else context.getString(R.string.message_no_subject)
                        setTypeface(null, style)
                    }
                    messageItemDate.run {
                        text = item.date.toFormattedString()
                        setTypeface(null, style)
                    }
                    messageItemAttachmentIcon.visibility =
                        if (item.hasAttachments) View.VISIBLE else View.GONE

                    root.setOnClickListener {
                        holder.bindingAdapterPosition.let {
                            if (it != NO_POSITION) onItemClickListener(
                                item,
                                it
                            )
                        }
                    }
                }
            }
            is HeaderViewHolder -> {
                with(holder.binding) {
                    if (onlyUnread == null) chipUnread.visibility = View.GONE
                    else {
                        chipUnread.isChecked = onlyUnread!!
                        chipUnread.setOnCheckedChangeListener(onHeaderClickListener)
                    }
                    chipAttachments.isChecked = onlyWithAttachments
                    chipAttachments.setOnCheckedChangeListener(onHeaderClickListener)
                }
            }
        }
    }

    class ItemViewHolder(val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root)
    class HeaderViewHolder(val binding: ItemMessageChipsBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class MessageTabDiffUtil(
        private val old: List<DataItem>,
        private val new: List<DataItem>
    ) :
        DiffUtil.Callback() {
        override fun getOldListSize(): Int = old.size

        override fun getNewListSize(): Int = new.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition].id == new[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition] == new[newItemPosition]
        }
    }
}

sealed class DataItem {
    data class MessageItem(val message: Message) : DataItem() {
        override val id = message.id
    }

    object Header : DataItem() {
        override val id = Long.MIN_VALUE
    }

    abstract val id: Long
}
