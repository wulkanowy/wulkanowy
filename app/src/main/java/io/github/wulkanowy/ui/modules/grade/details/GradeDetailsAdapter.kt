package io.github.wulkanowy.ui.modules.grade.details

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.enums.GradeColorTheme
import io.github.wulkanowy.data.enums.GradeExpandMode
import io.github.wulkanowy.databinding.HeaderGradeDetailsBinding
import io.github.wulkanowy.databinding.ItemGradeDetailsBinding
import io.github.wulkanowy.ui.base.BaseExpandableAdapter
import io.github.wulkanowy.utils.getBackgroundColor
import io.github.wulkanowy.utils.getCompatColor
import io.github.wulkanowy.utils.toFormattedString
import timber.log.Timber
import java.util.BitSet
import javax.inject.Inject

class GradeDetailsAdapter @Inject constructor() : BaseExpandableAdapter<RecyclerView.ViewHolder>() {

    private var headers = mutableListOf<GradeDetailsItem.Header>()
    private var items = mutableListOf<GradeDetailsItem>()

    private val expandedPositions = BitSet(items.size)
    private var expandMode = GradeExpandMode.ONE

    var onClickListener: (Grade, position: Int) -> Unit = { _, _ -> }

    lateinit var gradeColorTheme: GradeColorTheme

    fun setDataItems(
        data: List<GradeDetailsItem.Header>,
        newExpandMode: GradeExpandMode = this.expandMode
    ) {
        // Prevent recreating all items and collapsing all items when changing layout unrelated
        // settings, such as grade color theme.
        if (headers == data && expandMode == newExpandMode) {
            // Still need to update the items but there are no structural changes.
            notifyItemRangeChanged(0, items.size)
            return
        }

        headers = data.toMutableList()
        expandMode = newExpandMode
        if (expandMode == GradeExpandMode.ALWAYS_EXPANDED) {
            expandedPositions.set(0, expandedPositions.size())
        } else {
            expandedPositions.clear()
        }
        recreateItems()
    }

    fun updateDetailsItem(position: Int, grade: Grade) {
        items[position] = GradeDetailsItem.Grade(grade)
        notifyItemChanged(position)
    }

    fun getHeaderItem(subject: String): GradeDetailsItem.Header {
        val candidates = headers.filter { it.subject == subject }

        if (candidates.size > 1) {
            Timber.e("Header with subject $subject found ${candidates.size} times! Expanded: $expandedPositions. Items: $candidates")
        }

        return candidates.first()
    }

    fun updateHeaderItem(item: GradeDetailsItem.Header) {
        val headerPosition = headers.indexOf(item)
        val itemPosition = items.indexOf(item)

        headers[headerPosition] = item
        items[itemPosition] = item
        notifyItemChanged(itemPosition)
    }

    private fun recreateItems() {
        val newItems = mutableListOf<GradeDetailsItem>()
        for ((i, header) in headers.withIndex()) {
            newItems.add(header)
            if (expandedPositions[i]) {
                newItems.addAll(header.grades)
            }
        }
        refreshList(newItems)
    }

    fun collapseAll() {
        if (expandMode == GradeExpandMode.ALWAYS_EXPANDED) {
            return
        }
        expandedPositions.clear()
        recreateItems()
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
            ViewType.HEADER.id -> HeaderViewHolder(
                HeaderGradeDetailsBinding.inflate(inflater, parent, false)
            )
            ViewType.ITEM.id -> ItemViewHolder(
                ItemGradeDetailsBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> bindHeaderViewHolder(
                holder = holder,
                header = items[position] as GradeDetailsItem.Header,
                position = position
            )

            is ItemViewHolder -> bindItemViewHolder(
                holder = holder, grade = (items[position] as GradeDetailsItem.Grade).grade
            )
        }
    }

    private fun bindHeaderViewHolder(
        holder: HeaderViewHolder,
        header: GradeDetailsItem.Header,
        position: Int
    ) {
        val context = holder.binding.root.context
        val item = items[position]
        val headerPosition = headers.indexOf(item)

        with(holder.binding) {
            gradeHeaderDivider.isVisible = holder.bindingAdapterPosition != 0
            with(gradeHeaderSubject) {
                text = header.subject
                maxLines = if (expandedPositions[headerPosition]) 2 else 1
            }
            gradeHeaderAverage.text = formatAverage(header.average, root.context.resources)
            gradeHeaderPointsSum.text =
                context.getString(R.string.grade_points_sum, header.pointsSum)
            gradeHeaderPointsSum.isVisible = !header.pointsSum.isNullOrEmpty()
            gradeHeaderNumber.text = context.resources.getQuantityString(
                R.plurals.grade_number_item,
                header.grades.size,
                header.grades.size
            )

            header.newGrades.let { newGrades ->
                if (newGrades > 0) {
                    gradeHeaderNote.isVisible = true
                    gradeHeaderNote.text = newGrades.toString()
                } else {
                    gradeHeaderNote.isVisible = false
                }
            }

            gradeHeaderContainer.isEnabled = expandMode != GradeExpandMode.ALWAYS_EXPANDED
            gradeHeaderContainer.setOnClickListener {
                expandGradeHeader(headerPosition, header, holder)
            }
        }
    }

    private fun expandGradeHeader(
        position: Int,
        header: GradeDetailsItem.Header,
        holder: HeaderViewHolder
    ) {
        val wasExpanded = expandedPositions[position]
        when (expandMode) {
            GradeExpandMode.ONE -> {
                expandedPositions.clear()
                expandedPositions[position] = !wasExpanded
            }

            GradeExpandMode.UNLIMITED -> expandedPositions[position] = !wasExpanded
            GradeExpandMode.ALWAYS_EXPANDED -> return
        }

        recreateItems()
        if (!wasExpanded) {
            // If it wasn't expanded, it will be now - scroll make sure it's all on screen
            scrollToHeaderWithSubItems(holder.bindingAdapterPosition, header.grades.size)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindItemViewHolder(holder: ItemViewHolder, grade: Grade) {
        val context = holder.binding.root.context

        with(holder.binding) {
            gradeItemValue.run {
                text = grade.entry
                backgroundTintList = ColorStateList.valueOf(
                    context.getCompatColor(grade.getBackgroundColor(gradeColorTheme))
                )
            }
            gradeItemDescription.text = when {
                grade.description.isNotBlank() -> grade.description
                grade.gradeSymbol.isNotBlank() -> grade.gradeSymbol
                else -> context.getString(R.string.all_no_description)
            }
            gradeItemDate.text = grade.date.toFormattedString()
            gradeItemWeight.text = "${context.getString(R.string.grade_weight)}: ${grade.weight}"
            gradeItemNote.isVisible = !grade.isRead

            root.setOnClickListener {
                holder.bindingAdapterPosition.let {
                    if (it != NO_POSITION) onClickListener(grade, it)
                }
            }
        }
    }

    private fun formatAverage(average: Double?, resources: Resources) =
        if (average == null || average == .0) {
            resources.getString(R.string.grade_no_average)
        } else {
            resources.getString(R.string.grade_average, average)
        }

    private class HeaderViewHolder(val binding: HeaderGradeDetailsBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class ItemViewHolder(val binding: ItemGradeDetailsBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class GradeDetailsDiffUtil(
        private val old: List<GradeDetailsItem>,
        private val new: List<GradeDetailsItem>
    ) : DiffUtil.Callback() {

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
