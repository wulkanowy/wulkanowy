package io.github.wulkanowy.ui.modules.grade.details

import android.annotation.SuppressLint
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.databinding.HeaderGradeDetailsBinding
import io.github.wulkanowy.databinding.ItemGradeDetailsBinding
import io.github.wulkanowy.utils.getBackgroundColor
import io.github.wulkanowy.utils.toFormattedString
import javax.inject.Inject

class GradeDetailsAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items = mutableListOf<GradeDetailsItem<*>>()

    var onClickListener: (Grade, position: Int) -> Unit = { _, _ -> }

    var isExpanded = false

    var colorTheme = ""

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = items[position].viewType.id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            GradeDetailsItem.ViewType.HEADER.id -> HeaderViewHolder(HeaderGradeDetailsBinding.inflate(inflater, parent, false))
            GradeDetailsItem.ViewType.ITEM.id -> ItemViewHolder(ItemGradeDetailsBinding.inflate(inflater, parent, false))
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> bindHeaderViewHolder(holder.binding, items[position].value as GradeDetailsHeader, position)
            is ItemViewHolder -> bindItemViewHolder(holder.binding, items[position].value as Grade, position)
        }
    }

    private var expandedPosition = -1

    private fun bindHeaderViewHolder(binding: HeaderGradeDetailsBinding, header: GradeDetailsHeader, position: Int) {
        val isSubjectExpanded = position == expandedPosition || !isExpanded

        with(binding) {
            gradeHeaderSubject.apply {
                text = header.subject
                maxLines = if (isSubjectExpanded) 2 else 1
            }
            gradeHeaderAverage.text = formatAverage(header.average, root.context.resources)
            gradeHeaderPointsSum.text = root.context.getString(R.string.grade_points_sum, header.pointsSum)
            gradeHeaderPointsSum.visibility = if (!header.pointsSum.isNullOrEmpty()) View.VISIBLE else View.GONE
            gradeHeaderNumber.text = root.context.resources.getQuantityString(R.plurals.grade_number_item, header.number, header.number)
            gradeHeaderNote.visibility = if (header.newGrades > 0) View.VISIBLE else View.GONE
            if (header.newGrades > 0) gradeHeaderNote.text = header.newGrades.toString(10)

            gradeHeaderContainer.isEnabled = isExpanded
            gradeHeaderContainer.setOnClickListener {
                val oldExpandedPosition = expandedPosition
                expandedPosition = if (oldExpandedPosition == position) -1 else position
                notifyItemChanged(oldExpandedPosition)
                if (expandedPosition != -1) {
                    notifyItemChanged(expandedPosition)
                }
            }

            with(gradeHeaderRecycler) {
                visibility = if (isSubjectExpanded) View.VISIBLE else View.GONE
                layoutManager = LinearLayoutManager(context)
                adapter = GradeDetailsAdapter().apply {
                    items = header.grades.toMutableList()
                    onClickListener = this@GradeDetailsAdapter.onClickListener
                }
            }
        }
    }

    fun collapseAll() {
        if (expandedPosition != -1) {
            val oldExpandedPosition = expandedPosition
            expandedPosition = -1
            notifyItemChanged(oldExpandedPosition)
        }
    }

    private fun formatAverage(average: Double?, resources: Resources): String {
        return if (average == null || average == .0) resources.getString(R.string.grade_no_average)
        else resources.getString(R.string.grade_average, average)
    }

    @SuppressLint("SetTextI18n")
    private fun bindItemViewHolder(binding: ItemGradeDetailsBinding, grade: Grade, position: Int) {
        with(binding) {
            gradeItemValue.run {
                text = grade.entry
                setBackgroundResource(grade.getBackgroundColor(colorTheme))
            }
            gradeItemDescription.text = when {
                grade.description.isNotBlank() -> grade.description
                grade.gradeSymbol.isNotBlank() -> grade.gradeSymbol
                else -> root.context.getString(R.string.all_no_description)
            }
            gradeItemDate.text = grade.date.toFormattedString()
            gradeItemWeight.text = "${root.context.getString(R.string.grade_weight)}: ${grade.weight}"
            gradeItemNote.visibility = if (!grade.isRead) View.VISIBLE else View.GONE

            root.setOnClickListener { onClickListener(grade, position) }
        }
    }

    private class HeaderViewHolder(val binding: HeaderGradeDetailsBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class ItemViewHolder(val binding: ItemGradeDetailsBinding) :
        RecyclerView.ViewHolder(binding.root)
}
