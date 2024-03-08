package io.github.wulkanowy.ui.modules.calculator.contextmenu

import android.content.Context
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.contextmenu.BaseContextMenu
import io.github.wulkanowy.ui.base.contextmenu.ContextMenuItem
import io.github.wulkanowy.ui.modules.calculator.Calculator
import io.github.wulkanowy.ui.modules.calculator.CalculatorItem
import io.github.wulkanowy.ui.modules.calculator.addedit.CalculatorAddEditDialog
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.utils.getCompatDrawable
import javax.inject.Inject

@AndroidEntryPoint
class CalculatorItemContextMenu : BaseContextMenu(), CalculatorContextMenuView {
    companion object {
        private const val ARGUMENT_KEY = "calculatorItemKey"
        fun newInstance(item: CalculatorItem) = CalculatorItemContextMenu().apply {
            arguments = Bundle().apply { putSerializable(ARGUMENT_KEY, item) }
        }
    }

    @Inject
    lateinit var presenter: CalculatorContextMenuPresenter

    @Inject
    lateinit var calculator: Calculator

    private val ctx: Context
        get() = requireContext()

    private val itemArgument: CalculatorItem
        get() = arguments?.get(ARGUMENT_KEY) as CalculatorItem

    override val deleteItem
        get() = ContextMenuItem(
            ctx.getString(R.string.all_delete),
            ctx.getCompatDrawable(R.drawable.ic_menu_message_delete)
        )

    override val editItem
        get() = ContextMenuItem(
            ctx.getString(R.string.all_edit),
            ctx.getCompatDrawable(R.drawable.ic_more_note)
        )

    override fun showEditDialog() {
        (activity as? MainActivity)?.showDialogFragment(
            CalculatorAddEditDialog.newInstance(itemArgument)
        )
    }

    override fun deleteItem() = calculator.deleteItem(itemArgument)

    override fun onViewCreated() {
        presenter.onAttachView(this)
    }

    override fun getItems(): MutableList<ContextMenuItem> = mutableListOf(editItem, deleteItem)

    override fun onItemClicked(item: ContextMenuItem) = when (item.title) {
        editItem.title -> presenter.onEditItemClicked()
        deleteItem.title -> presenter.onDeleteItemClicked()
        else -> throw IllegalStateException()
    }
}
