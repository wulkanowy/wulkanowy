package io.github.wulkanowy.ui.modules.calculator

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.FragmentCalculatorBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.calculator.addedit.CalculatorAddEditDialog
import io.github.wulkanowy.ui.modules.calculator.contextmenu.CalculatorItemContextMenu
import io.github.wulkanowy.ui.modules.calculator.options.CalculatorOptionsDialog
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.widgets.DividerItemDecoration
import javax.inject.Inject

@AndroidEntryPoint
class CalculatorFragment : BaseFragment<FragmentCalculatorBinding>(R.layout.fragment_calculator),
    CalculatorView, MainView.MainChildView, MainView.TitledView {

    companion object {
        fun newInstance() = CalculatorFragment()
    }

    @Inject
    lateinit var calculatorPresenter: CalculatorPresenter

    @Inject
    lateinit var calculator: Calculator

    @Inject
    lateinit var calculatorAdapter: CalculatorAdapter

    override val titleStringId: Int = R.string.calculator_title

    override fun initView() {
        calculator.onListUpdated = { updateData(calculator.defaultList) }

        with(binding.calculatorRecycler) {
            layoutManager = LinearLayoutManager(context)
            calculatorAdapter.onItemClick = { showContextMenu(it) }
            adapter = calculatorAdapter
            addItemDecoration(DividerItemDecoration(context))
            updateData(calculator.defaultList)
        }
        with(binding) {
            calculatorAddGradeButton.setOnClickListener { calculatorPresenter.onAddButtonClicked() }
        }
    }

    private fun showContextMenu(item: CalculatorItem) {
        (activity as? MainActivity)?.showDialogFragment(
            CalculatorItemContextMenu.newInstance(item)
        )
    }

    override fun updateData(calculatorItemList: CalculatorItemList) {
        with(calculatorAdapter) {
            calculatorPresenter.updateData(calculatorItemList)
            notifyDataSetChanged()
        }
    }

    override fun showItemDialog(calculatorItem: CalculatorItem) {
        (activity as? MainActivity)?.showDialogFragment(
            CalculatorItemDialog.newInstance(calculatorItem)
        )
    }

    override fun showAddDialog() {
        (activity as? MainActivity)?.showDialogFragment(
            CalculatorAddEditDialog.newInstance(null)
        )
    }

    override fun showEmpty() {
        binding.calculatorNoGrades.visibility = VISIBLE
    }

    override fun hideEmpty() {
        binding.calculatorNoGrades.visibility = GONE
    }

    override fun showList() {
        binding.calculatorRecycler.visibility = VISIBLE
    }

    override fun hideList() {
        binding.calculatorRecycler.visibility = GONE
    }

    override fun showOptionsDialog() {
        (activity as? MainActivity)?.showDialogFragment(CalculatorOptionsDialog.newInstance())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCalculatorBinding.bind(view)
        calculatorPresenter.onAttachView(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_menu_calculator, menu)
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.calculatorMenuOptions -> calculatorPresenter.onOptionsSelected()
            R.id.calculatorMenuClear -> calculatorPresenter.onClearSelected()
            else -> false
        }
    }

    override fun clearCalculator() = calculator.clear()

    override fun onFragmentReselected() {}
}
