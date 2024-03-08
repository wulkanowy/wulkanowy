package io.github.wulkanowy.ui.modules.calculator.addedit

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.DialogCalculatorItemAddBinding
import io.github.wulkanowy.ui.base.BaseDialogFragment
import io.github.wulkanowy.ui.modules.calculator.Calculator
import io.github.wulkanowy.ui.modules.calculator.CalculatorItem
import io.github.wulkanowy.utils.round
import javax.inject.Inject

@AndroidEntryPoint
class CalculatorAddEditDialog : BaseDialogFragment<DialogCalculatorItemAddBinding>(),
    CalculatorAddEditView {

    @Inject
    lateinit var presenter: CalculatorAddEditPresenter

    @Inject
    lateinit var calculator: Calculator

    private val calculatorItem
        get() = arguments?.get(ITEM_ARGUMENT_KEY) as CalculatorItem?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
    }

    companion object {
        private const val ITEM_ARGUMENT_KEY = "CalculatorItemArgumentKey"
        fun newInstance(item: CalculatorItem?) = CalculatorAddEditDialog().apply {
            arguments = Bundle().apply { putSerializable(ITEM_ARGUMENT_KEY, item) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = DialogCalculatorItemAddBinding.inflate(inflater).apply { binding = this }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogCalculatorItemAddBinding.bind(view)
        presenter.onAttachView(this)
    }

    override fun closeDialog() {
        dismiss()
    }

    private val ctx: Context
        get() = requireContext()

    private fun bindForAdding() {
        with(binding) {
            calculatorItemAddDialogAdd.setOnClickListener { presenter.onAddItemClicked() }
            calculatorItemAddDialogAdd.text = ctx.getString(R.string.all_add)
            addCalculatorItemHeader.text =
                ctx.getString(R.string.calculator_item_add)
        }
    }

    private fun bindTextBoxesFromItem() {
        val item = calculatorItem!!
        binding.calculatorItemAddDialogGradeEdit.setText(
            item.originalGrade
                ?: calculatorItem!!.grade.round(3).toString()
        )
        binding.calculatorItemAddDialogTitleEdit.setText(item.title ?: "")
        binding.calculatorItemAddDialogWeightEdit.setText(item.weight.round(3).toString())
    }

    private fun bindForUpdating() {
        bindTextBoxesFromItem()
        with(binding) {
            calculatorItemAddDialogAdd.setOnClickListener { presenter.onUpdateItemClicked() }
            calculatorItemAddDialogAdd.text = ctx.getString(R.string.all_edit)
            addCalculatorItemHeader.text = ctx.getString(R.string.calculator_item_edit)
        }
    }

    private fun bindDeletingErrorsWhenTyping() {
        with(binding) {
            calculatorItemAddDialogWeightEdit.run {
                doOnTextChanged { _, _, _, _ ->
                    error = null
                }
            }
            calculatorItemAddDialogGradeEdit.run {
                doOnTextChanged { _, _, _, _ ->
                    error = null
                }
            }
        }
    }

    private fun bindCommons() {
        binding.calculatorItemAddDialogClose.setOnClickListener { closeDialog() }
        bindDeletingErrorsWhenTyping()
    }

    override fun initView() {
        when (calculatorItem) {
            null -> bindForAdding()
            else -> bindForUpdating()
        }
        bindCommons()
    }

    override val gradeText: String
        get() = binding.calculatorItemAddDialogGradeEdit.text.toString()

    override val titleText: String
        get() = binding.calculatorItemAddDialogTitleEdit.text.toString()

    override val weightText: String
        get() = binding.calculatorItemAddDialogWeightEdit.text.toString()

    override fun updateItem(title: String?, grade: Double, weight: Double, originalGrade: String?) {
        with(calculatorItem!!) {
            this.title = title
            this.weight = weight
            this.grade = grade
            this.originalGrade = originalGrade
        }

        calculator.listUpdated()
    }

    override fun saveNewItem(
        title: String?,
        grade: Double,
        weight: Double,
        originalGrade: String?
    ) {
        val newItem = calculator.makeItem(title, grade, weight, originalGrade)
        calculator.addItem(newItem)
    }

    override fun showGradeError(gradeText: String) {
        with(binding.calculatorItemAddDialogGradeEdit) {
            error = ctx.getString(R.string.calculator_invalid_grade_error)
        }
    }

    override fun showWeightError(weightText: String) {
        with(binding.calculatorItemAddDialogWeightEdit) {
            error = ctx.getString(R.string.calculator_invalid_weight_error)
        }
    }
}
