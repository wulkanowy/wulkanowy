package io.github.wulkanowy.ui.modules.timetable.realized

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Realized
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_realized.*

class RealizedItem(val realized: Realized) : AbstractFlexibleItem<RealizedItem.ViewHolder>() {

    override fun getLayoutRes() = R.layout.item_realized

    override fun createViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<*>>?): RealizedItem.ViewHolder {
        return RealizedItem.ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>?, holder: RealizedItem.ViewHolder?, position: Int, payloads: MutableList<Any>?) {
        holder?.apply {
            realizedItemNumber.text = realized.number.toString()
            realizedItemSubject.text = realized.subject
            realizedItemTopic.text = realized.topic
            realizedItemAlert.visibility = if (realized.substitution.isNotEmpty()) VISIBLE else GONE
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RealizedItem

        if (realized != other.realized) return false

        return true
    }

    override fun hashCode(): Int {
        return realized.hashCode()
    }

    class ViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<*>>?) : FlexibleViewHolder(view, adapter),
        LayoutContainer {

        override val containerView: View?
            get() = contentView
    }
}
