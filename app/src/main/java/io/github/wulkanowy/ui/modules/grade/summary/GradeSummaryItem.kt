package io.github.wulkanowy.ui.modules.grade.summary

import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_grade_summary.*

class GradeSummaryItem(
    private val title: String,
    private val average: String,
    private val predicted: String,
    private val final: String
) : AbstractFlexibleItem<GradeSummaryItem.ViewHolder>() {

    override fun getLayoutRes() = R.layout.item_grade_summary

    override fun createViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<*>>?): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<*>>?, holder: ViewHolder?,
        position: Int, payloads: MutableList<Any>?
    ) {
        holder?.run {
            gradeSummaryItemTitle.text = title
            gradeSummaryItemAverage.text = average
            gradeSummaryItemPredicted.text = predicted
            gradeSummaryItemFinal.text = final
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GradeSummaryItem

        if (average != other.average) return false
        if (title != other.title) return false
        if (predicted != other.predicted) return false
        if (final != other.final) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + average.hashCode()
        result = 31 * result + predicted.hashCode()
        result = 31 * result + final.hashCode()
        return result
    }

    class ViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<*>>?) : FlexibleViewHolder(view, adapter),
        LayoutContainer {

        override val containerView: View?
            get() = contentView
    }
}
