package io.github.wulkanowy.ui.modules.calculator

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.ItemCalculatorBinding
import io.github.wulkanowy.databinding.ScrollableHeaderCalculatorAverageBinding
import io.github.wulkanowy.utils.calculateAverage
import io.github.wulkanowy.utils.round
import javax.inject.Inject

class CalculatorAdapter @Inject constructor(@ApplicationContext val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        val differ = object : DiffUtil.ItemCallback<CalculatorItem>() {
            override fun areItemsTheSame(
                oldItem: CalculatorItem,
                newItem: CalculatorItem
            ): Boolean {

                return oldItem.grade == newItem.grade
                    && oldItem.date == newItem.date
                    && oldItem.title == newItem.title
                    && oldItem.weight == newItem.weight
                    && oldItem.originalGrade == newItem.originalGrade

            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: CalculatorItem,
                newItem: CalculatorItem
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    private enum class ViewType(val id: Int) {
        HEADER(0),
        ITEM(1)
    }

    @Inject
    lateinit var calculator: Calculator

    var onItemClick: (CalculatorItem) -> Unit = {}

    private class ItemViewHolder(val binding: ItemCalculatorBinding) :
        RecyclerView.ViewHolder(binding.root)

    private class HeaderViewHolder(val binding: ScrollableHeaderCalculatorAverageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ViewType.HEADER.id -> HeaderViewHolder(
                ScrollableHeaderCalculatorAverageBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            ViewType.ITEM.id -> ItemViewHolder(
                ItemCalculatorBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            else -> throw IllegalStateException()
        }
    }

    private fun getItem(position: Int): CalculatorItem = calculator.defaultList[position]

    private fun totalItems() = calculator.defaultList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> bindHeaderViewHolder(holder.binding)
            is ItemViewHolder -> bindItemViewHolder(holder.binding, getItem(position - 1))
            else -> throw IllegalStateException()
        }
    }

    private fun bindHeaderViewHolder(binding: ScrollableHeaderCalculatorAverageBinding) {
        binding.calculatorAverageTotalGradesCalculated.text =
            context.getString(R.string.calculator_average_from_x_items, totalItems())
        binding.calculatorAverageResult.text =
            calculator.defaultList.calculateAverage().round(3).toString()
    }


    override fun getItemViewType(position: Int): Int = when (position) {
        0 -> ViewType.HEADER.id
        else -> ViewType.ITEM.id
    }

    override fun getItemCount(): Int =
        totalItems() + if (calculator.defaultList.isEmpty()) 0 else 1

    private fun getItemTitleOrFallbackValue(title: String?): String =
        if (title.isNullOrEmpty()) context.getString(R.string.calculator_item_title_fallback)
        else title

    private fun bindItemViewHolder(binding: ItemCalculatorBinding, item: CalculatorItem) {
        binding.calculatorItemWeight.text =
            context.getString(R.string.calculator_item_weight, item.weight.round(3).toString())
        binding.calculatorItemGrade.text =
            context.getString(R.string.calculator_item_grade, item.grade.round(3).toString())
        binding.calculatorItemTitle.text = getItemTitleOrFallbackValue(item.title)
        binding.calculatorItemOriginalGrade.text =
            item.originalGrade ?: item.grade.toString().take(4)
        binding.root.setOnClickListener { onItemClick(item) }
    }
}
