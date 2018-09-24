package io.github.wulkanowy.ui.main.timetable

import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractExpandableItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.ExpandableViewHolder
import io.github.wulkanowy.R
import io.github.wulkanowy.utils.extension.getWeekDayName
import io.github.wulkanowy.utils.extension.toFormat
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.header_timetable.*
import java.util.*

class TimetableHeader : AbstractExpandableItem<TimetableHeader.ViewHolder, TimetableItem>() {

    lateinit var date: Date

    override fun createViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<*>>?): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun getLayoutRes() = R.layout.header_timetable

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TimetableHeader

        if (date != other.date) return false

        return true
    }

    override fun hashCode(): Int {
        return date.hashCode()
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>?, holder: ViewHolder,
                                position: Int, payloads: MutableList<Any>?) {
        holder.run {
            timetableHeaderDay.text = date.getWeekDayName().capitalize()
            timetableHeaderDate.text = date.toFormat()
            timetableHeaderAlert.visibility = View.GONE
        }
    }

    class ViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<*>>?) : ExpandableViewHolder(view, adapter),
            LayoutContainer {

        init {
            contentView.setOnClickListener(this)
        }

        override fun shouldNotifyParentOnClick() = true

        override val containerView: View
            get() = contentView
    }
}
