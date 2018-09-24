package io.github.wulkanowy.ui.main.grade.details

import android.view.View
import android.view.View.GONE
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractExpandableItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.ExpandableViewHolder
import io.github.wulkanowy.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.header_grade_details.*

class GradeDetailsHeader : AbstractExpandableItem<GradeDetailsHeader.ViewHolder, GradeDetailsItem>() {

    lateinit var subject: String

    lateinit var number: String

    lateinit var average: String

    override fun createViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<*>>?): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun getLayoutRes() = R.layout.header_grade_details

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GradeDetailsHeader

        if (subject != other.subject) return false

        return true
    }

    override fun hashCode(): Int {
        return subject.hashCode()
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>?, holder: ViewHolder,
                                position: Int, payloads: MutableList<Any>?) {
        holder.run {
            gradeHeaderSubject.text = subject
            gradeHeaderAverage.text = average
            gradeHeaderNumber.text = number
            gradeHeaderPredicted.visibility = GONE
            gradeHeaderFinal.visibility = GONE

            gradeHeaderNote.visibility = GONE
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
