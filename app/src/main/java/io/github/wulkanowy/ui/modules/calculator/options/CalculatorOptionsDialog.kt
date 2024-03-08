package io.github.wulkanowy.ui.modules.calculator.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.databinding.DialogCalculatorOptionsBinding
import io.github.wulkanowy.ui.base.BaseDialogFragment
import io.github.wulkanowy.ui.modules.calculator.Calculator
import javax.inject.Inject

@AndroidEntryPoint
class CalculatorOptionsDialog : BaseDialogFragment<DialogCalculatorOptionsBinding>(),
    CalculatorOptionsView {

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    @Inject
    lateinit var calculator: Calculator

    @Inject
    lateinit var presenter: CalculatorOptionsPresenter

    override fun readMinusInput(): Double? =
        binding.calculatorOptionsDialogMinusValueEdit.text.toString().toDoubleOrNull()

    override fun readPlusInput(): Double? =
        binding.calculatorOptionsDialogPlusValueEdit.text.toString().toDoubleOrNull()

    override fun showMinusInputError() {
        binding.calculatorOptionsDialogMinusValue.error =
            requireContext().getString(R.string.invalid_minus_or_plus_modifier)
    }

    override fun showPlusInputError() {
        binding.calculatorOptionsDialogPlusValue.error =
            requireContext().getString(R.string.invalid_minus_or_plus_modifier)
    }

    override fun closeDialog() = dismiss()

    override fun initView() {
        binding.calculatorOptionsDialogPlusValueEdit.setText(calculator.getPlusValue().toString())

        binding.calculatorOptionsDialogMinusValueEdit.setText(calculator.getMinusValue().toString())

        binding.calculatorOptionsDialogPlusValueEdit.doOnTextChanged { _, _, _, _ ->
            binding.calculatorOptionsDialogPlusValueEdit.error = null
        }

        binding.calculatorOptionsDialogMinusValueEdit.doOnTextChanged { _, _, _, _ ->
            binding.calculatorOptionsDialogMinusValueEdit.error = null
        }

        binding.calculatorOptionsDialogSave.setOnClickListener {
            presenter.onSaveButtonClicked()
        }

        binding.calculatorOptionsDialogClose.setOnClickListener { presenter.onCloseButtonClicked() }
    }

    override fun resetCalculator() {
        calculator.defaultList.clear()
        calculator.listUpdated()
    }


    companion object {
        fun newInstance() = CalculatorOptionsDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = DialogCalculatorOptionsBinding.inflate(inflater).apply { binding = this }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogCalculatorOptionsBinding.bind(view)
        presenter.onAttachView(this)
    }
}
