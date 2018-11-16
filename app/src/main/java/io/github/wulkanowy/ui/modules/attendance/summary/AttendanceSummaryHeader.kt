package io.github.wulkanowy.ui.modules.attendance.summary

import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractHeaderItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.header_grade_summary.*

class AttendanceSummaryHeader(private val name: String, private val value: String) : AbstractHeaderItem<AttendanceSummaryHeader.ViewHolder>() {

    override fun getLayoutRes() = R.layout.header_grade_summary

    override fun createViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<*>>?): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>?, holder: ViewHolder?,
        position: Int, payloads: MutableList<Any>?) {
        holder?.run {
            gradeSummaryHeaderName.text = name
            gradeSummaryHeaderAverage.text = value
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AttendanceSummaryHeader

        if (name != other.name) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }

    class ViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<*>>?) :
        FlexibleViewHolder(view, adapter), LayoutContainer {

        override val containerView: View?
            get() = contentView
    }
}
