package io.github.wulkanowy.ui.modules.grade.details

import android.annotation.SuppressLint
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.repositories.GradeExpandMode
import io.github.wulkanowy.databinding.HeaderGradeDetailsBinding
import io.github.wulkanowy.databinding.ItemGradeDetailsBinding
import io.github.wulkanowy.ui.base.BaseExpandableAdapter
import io.github.wulkanowy.utils.getBackgroundColor
import io.github.wulkanowy.utils.toFormattedString
import timber.log.Timber
import java.util.BitSet
import javax.inject.Inject

class GradeDetailsAdapter @Inject constructor() : BaseExpandableAdapter<RecyclerView.ViewHolder>() {

    private var headers = mutableListOf<GradeDetailsItem>()

    private var items = mutableListOf<GradeDetailsItem>()

    private val expandedPositions = BitSet(items.size)

    private var expandMode: GradeExpandMode = GradeExpandMode.AlwaysExpanded

    var onClickListener: (Grade, position: Int) -> Unit = { _, _ -> }

    var colorTheme = ""

    fun setDataItems(data: List<GradeDetailsItem>, expandMode: GradeExpandMode = this.expandMode) {
        headers = data.filter { it.viewType == ViewType.HEADER }.toMutableList()
        items = (if (expandMode.isExpandable) headers else data).toMutableList()
        this.expandMode = expandMode
        expandedPositions.clear()
    }

    fun updateDetailsItem(position: Int, grade: Grade) {
        items[position] = GradeDetailsItem(grade, ViewType.ITEM)
        notifyItemChanged(position)
    }

    fun getHeaderItem(subject: String): GradeDetailsItem {
        val candidates = headers.filter { (it.value as GradeDetailsHeader).subject == subject }

        if (candidates.size > 1) {
            Timber.e("Header with subject $subject found ${candidates.size} times! Expanded: $expandedPositions. Items: $candidates")
        }

        return candidates.first()
    }

    fun updateHeaderItem(item: GradeDetailsItem) {
        val headerPosition = headers.indexOf(item)
        val itemPosition = items.indexOf(item)

        headers[headerPosition] = item
        items[itemPosition] = item
        notifyItemChanged(itemPosition)
    }

    fun collapseAll() {
        if (!expandedPositions.isEmpty) {
            refreshList(headers)
            expandedPositions.clear()
        }
    }

    @Synchronized
    private fun refreshList(newItems: MutableList<GradeDetailsItem>) {
        val diffCallback = GradeDetailsDiffUtil(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = items[position].viewType.id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            ViewType.HEADER.id -> HeaderViewHolder(HeaderGradeDetailsBinding.inflate(inflater, parent, false))
            ViewType.ITEM.id -> ItemViewHolder(ItemGradeDetailsBinding.inflate(inflater, parent, false))
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> bindHeaderViewHolder(
                holder = holder,
                header = items[position].value as GradeDetailsHeader,
                position = position
            )
            is ItemViewHolder -> bindItemViewHolder(
                holder = holder,
                grade = items[position].value as Grade
            )
        }
    }

    private fun bindHeaderViewHolder(holder: HeaderViewHolder, header: GradeDetailsHeader, position: Int) {
        val item = items[position]
        val headerPosition = headers.indexOf(item)
        val adapterPosition = holder.bindingAdapterPosition

        with(holder.binding) {
            gradeHeaderDivider.visibility = if (adapterPosition == 0) View.GONE else View.VISIBLE
            with(gradeHeaderSubject) {
                text = header.subject
                maxLines = if (expandedPositions[headerPosition]) 2 else 1
            }
            gradeHeaderAverage.text = formatAverage(header.average, root.context.resources)
            gradeHeaderPointsSum.text = root.context.getString(R.string.grade_points_sum, header.pointsSum)
            gradeHeaderPointsSum.visibility = if (!header.pointsSum.isNullOrEmpty()) View.VISIBLE else View.GONE
            gradeHeaderNumber.text = root.context.resources.getQuantityString(R.plurals.grade_number_item, header.grades.size, header.grades.size)
            gradeHeaderNote.visibility = if (header.newGrades > 0) View.VISIBLE else View.GONE
            if (header.newGrades > 0) gradeHeaderNote.text = header.newGrades.toString(10)

            gradeHeaderContainer.isEnabled = expandMode.isExpandable
            gradeHeaderContainer.setOnClickListener {
                if (expandMode == GradeExpandMode.One) {
                    val oldExpandedHeaderPos = expandedPositions.nextSetBit(0)
                    val newExpanded = !expandedPositions[headerPosition]
                    expandedPositions.clear()
                    if (newExpanded) {
                        if (oldExpandedHeaderPos != -1) {
                            val oldHeader = headers[oldExpandedHeaderPos].value as GradeDetailsHeader
                            items.subList(oldExpandedHeaderPos + 1, oldExpandedHeaderPos + 1 + oldHeader.grades.size).clear()
                            notifyItemRangeRemoved(oldExpandedHeaderPos + 1, oldHeader.grades.size)
                        }
                        expandedPositions.set(headerPosition)
                        items.addAll(headerPosition + 1, header.grades)
                        notifyItemRangeInserted(headerPosition + 1, header.grades.size)
                    } else {
                        items.subList(headerPosition + 1, headerPosition + 1 + header.grades.size).clear()
                        notifyItemRangeRemoved(headerPosition + 1, header.grades.size)
                    }
                } else if (expandMode == GradeExpandMode.Unlimited) {
                    expandedPositions.flip(headerPosition)

                    // Once this listener is invoked, there may have been other grades expanded
                    // thus invalidating the `position` argument from the initial method call
                    val newPosition = items.indexOf(item)
                    if (expandedPositions[headerPosition]) {
                        items.addAll(newPosition + 1, header.grades)
                        notifyItemRangeInserted(newPosition + 1, header.grades.size)
                        scrollToHeaderWithSubItems(newPosition, header.grades.size)
                    } else {
                        items.subList(newPosition + 1, newPosition + 1 + header.grades.size).clear()
                        notifyItemRangeRemoved(newPosition + 1, header.grades.size)
                    }
                }
            }
        }
    }

    private fun formatAverage(average: Double?, resources: Resources): String {
        return if (average == null || average == .0) resources.getString(R.string.grade_no_average)
        else resources.getString(R.string.grade_average, average)
    }

    @SuppressLint("SetTextI18n")
    private fun bindItemViewHolder(holder: ItemViewHolder, grade: Grade) {
        with(holder.binding) {
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

            root.setOnClickListener {
                holder.bindingAdapterPosition.let { if (it != NO_POSITION) onClickListener(grade, it) }
            }
        }
    }

    private class HeaderViewHolder(val binding: HeaderGradeDetailsBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class ItemViewHolder(val binding: ItemGradeDetailsBinding) :
        RecyclerView.ViewHolder(binding.root)

    class GradeDetailsDiffUtil(private val old: List<GradeDetailsItem>, private val new: List<GradeDetailsItem>) :
        DiffUtil.Callback() {

        override fun getOldListSize() = old.size

        override fun getNewListSize() = new.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition] == new[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition] == new[newItemPosition]
        }
    }
}
