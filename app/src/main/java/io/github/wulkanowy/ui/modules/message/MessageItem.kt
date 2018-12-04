package io.github.wulkanowy.ui.modules.message

import android.graphics.Typeface
import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_message.*

class MessageItem(var message: Message, private val noSubjectString: String) :
    AbstractFlexibleItem<MessageItem.ViewHolder>() {

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun getLayoutRes(): Int = R.layout.item_message

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<*>>, holder: ViewHolder,
        position: Int, payloads: MutableList<Any>?
    ) {
        holder.apply {
            messageItemAuthor.run {
                text = if (message.recipient?.isNotBlank() == true) message.recipient else message.sender
                setTypeface(null, if (message.unread == true) Typeface.BOLD else Typeface.NORMAL)
            }
            messageItemSubject.run {
                text = if (message.subject.isNotBlank()) message.subject else noSubjectString
                setTypeface(null, if (message.unread == true) Typeface.BOLD else Typeface.NORMAL)
            }
            messageItemDate.run {
                text = message.date?.toFormattedString()
                setTypeface(null, if (message.unread == true) Typeface.BOLD else Typeface.NORMAL)
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MessageItem

        if (message != other.message) return false
        return true
    }

    override fun hashCode(): Int {
        return message.hashCode()
    }

    class ViewHolder(val view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter),
        LayoutContainer {

        override val containerView: View
            get() = contentView
    }
}
