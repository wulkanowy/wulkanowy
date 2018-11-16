package io.github.wulkanowy.ui.modules.attendance.summary

import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractSectionableItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_grade_summary.*

class AttendanceSummaryItem(header: AttendanceSummaryHeader, private val name: String, private val value: Int) :
    AbstractSectionableItem<AttendanceSummaryItem.ViewHolder, AttendanceSummaryHeader>(header) {

    override fun getLayoutRes() = R.layout.item_grade_summary

    override fun createViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<*>>?): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<*>>?, holder: ViewHolder?,
        position: Int, payloads: MutableList<Any>?
    ) {
        holder?.run {
            gradeSummaryItemGrade.text = value.toString()
            gradeSummaryItemTitle.text = name
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AttendanceSummaryItem

        if (value != other.value) return false
        if (name != other.name) return false
        if (header != other.header) return false

        return true
    }

    override fun hashCode(): Int {
        var result = header.hashCode()
        result = 31 * result + value.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    class ViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<*>>?) : FlexibleViewHolder(view, adapter),
        LayoutContainer {

        override val containerView: View?
            get() = contentView
    }
}
