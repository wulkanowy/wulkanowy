package io.github.wulkanowy.ui.modules.message.tab

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.repositories.message.MessageFolder
import io.github.wulkanowy.databinding.ItemMessageBinding
import io.github.wulkanowy.utils.toFormattedString
import javax.inject.Inject

class MessageTabAdapter @Inject constructor() :
    RecyclerView.Adapter<MessageTabAdapter.ItemViewHolder>() {

    var onClickListener: (Message, position: Int) -> Unit = { _, _ -> }

    private val items = mutableListOf<MessageSearchMatch>()

    fun replaceAll(models: List<MessageSearchMatch>) {
        for (i in items.size - 1 downTo 0) {
            val item = items.get(i)
            if (models.find { it.message.id == item.message.id } == null) {
                items.removeAt(i)
                notifyItemRemoved(i)
            }
        }

        models.forEachIndexed { index, model ->
            if (items.find { it.message.id == model.message.id } == null) {
                items.add(index, model)
                notifyItemInserted(index)
            }
        }

        models.forEachIndexed { index, model ->
            val indexOfItem = items.indexOfFirst { it.message.id == model.message.id }
            val item = items.get(indexOfItem)
            if (indexOfItem != index) {
                for (i in indexOfItem - 1 downTo index) {
                    items.set(i + 1, items.get(i))
                }

                items.set(index, model)

                notifyItemMoved(indexOfItem, index)
            }

            if (item.message.hashCode() != model.message.hashCode()) {
                notifyItemChanged(index)
            }
        }
    }

    fun updateItem(position: Int, item: Message) {
        val currentItem = items.get(position)
        items.set(position, MessageSearchMatch(item, currentItem.query))
        if (item.hashCode() != currentItem.message.hashCode()) {
            notifyItemChanged(position)
        }
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]

        with(holder.binding) {
            val style = if (item.message.unread) Typeface.BOLD else Typeface.NORMAL

            messageItemAuthor.run {
                text = if (item.message.folderId == MessageFolder.SENT.id) item.message.recipient else item.message.sender
                setTypeface(null, style)
            }
            messageItemSubject.run {
                text = if (item.message.subject.isNotBlank()) item.message.subject else context.getString(R.string.message_no_subject)
                setTypeface(null, style)
            }
            messageItemDate.run {
                text = item.message.date.toFormattedString()
                setTypeface(null, style)
            }
            messageItemAttachmentIcon.visibility = if (item.message.hasAttachments) View.VISIBLE else View.GONE

            root.setOnClickListener {
                holder.adapterPosition.let { if (it != NO_POSITION) onClickListener(item.message, it) }
            }
        }
    }

    class ItemViewHolder(val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root)
}
