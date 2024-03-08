package io.github.wulkanowy.ui.modules.grade.details.contextmenu

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.ui.base.contextmenu.BaseContextMenu
import io.github.wulkanowy.ui.base.contextmenu.ContextMenuItem
import io.github.wulkanowy.ui.modules.calculator.Calculator
import io.github.wulkanowy.ui.modules.calculator.CalculatorFragment
import io.github.wulkanowy.ui.modules.grade.details.SerializableGradleList
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.utils.getCompatDrawable
import javax.inject.Inject

@AndroidEntryPoint
class GradeHeaderContextMenu : BaseContextMenu(), GradeHeaderContextMenuView {
    @Inject
    lateinit var presenter: GradeHeaderContextMenuPresenter

    @Inject
    lateinit var calculator: Calculator

    companion object {
        private const val ItemKey = "SerializableGradeList"
        fun newInstance(serializableGradeList: SerializableGradleList) =
            GradeHeaderContextMenu().apply {
                arguments = Bundle().apply { putSerializable(ItemKey, serializableGradeList) }
            }
    }

    private val gradeList: SerializableGradleList
        get() = arguments?.getSerializable(ItemKey) as SerializableGradleList

    override fun onViewCreated() {
        presenter.onAttachView(this)
    }

    override fun getItems(): MutableList<ContextMenuItem> =
        mutableListOf(newCalculatorItem, appendToCalculatorItem)

    override fun onItemClicked(item: ContextMenuItem) = when (item.title) {
        newCalculatorItem.title -> presenter.newCalculator()
        appendToCalculatorItem.title -> presenter.appendCalculator()
        else -> throw IllegalStateException()
    }

    private val ctx get() = requireContext()

    override fun appendCalculator() {
        val calcItems = gradeList.list.map {
            calculator.makeItem(it.description, it.value, it.weightValue, it.entry)
        }

        calculator.addItems(calcItems)
    }

    override val newCalculatorItem: ContextMenuItem get() = ContextMenuItem(
        ctx.getString(R.string.new_calculator_from_grades),
        ctx.getCompatDrawable(R.drawable.ic_all_add)
    )

    override val appendToCalculatorItem: ContextMenuItem
        get() = ContextMenuItem(
            ctx.getString(R.string.append_calculator),
            ctx.getCompatDrawable(R.drawable.ic_all_add)
        )

    override fun clearCalculator() {
        calculator.defaultList.clear()
    }

    override fun openCalculator() {
        (activity as? MainActivity)?.pushView(CalculatorFragment.newInstance())
    }
}
