package io.github.wulkanowy.ui.main.grade.details

import android.view.View
import android.view.View.GONE
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Grade
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_grade_details.*

class GradeDetailsItem : AbstractFlexibleItem<GradeDetailsItem.ViewHolder>() {

    lateinit var grade: Grade

    lateinit var weightString: String

    var valueColor = 0

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun getLayoutRes() = R.layout.item_grade_details

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GradeDetailsItem

        if (grade != other.grade) return false
        return true
    }

    override fun hashCode(): Int {
        return grade.hashCode()
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: ViewHolder,
                                position: Int, payloads: MutableList<Any>?) {
        holder.run {
            gradeItemValue.run {
                text = grade.value
                setBackgroundResource(valueColor)
            }
            gradeItemDescription.text = if (grade.description.isNotEmpty()) grade.description else grade.gradeSymbol
            gradeItemDate.text = grade.date
            gradeItemWeight.text = "%s: %s".format(weightString, grade.weight)
            gradeItemNote.visibility = GONE
        }
    }

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter),
            LayoutContainer {

        override val containerView: View
            get() = contentView
    }
}
