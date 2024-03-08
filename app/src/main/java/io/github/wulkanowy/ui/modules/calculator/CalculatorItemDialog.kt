package io.github.wulkanowy.ui.modules.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.DialogCalculatorItemBinding
import io.github.wulkanowy.utils.lifecycleAwareVariable
import io.github.wulkanowy.utils.round
import javax.inject.Inject

@AndroidEntryPoint
class CalculatorItemDialog : DialogFragment() {
    private var binding: DialogCalculatorItemBinding by lifecycleAwareVariable()

    private lateinit var item: CalculatorItem

    @Inject
    lateinit var calculator: Calculator

    companion object {
        private const val ARGUMENT_KEY = "Item"
        fun newInstance(item: CalculatorItem) = CalculatorItemDialog().apply {
            arguments = Bundle().apply { putSerializable(ARGUMENT_KEY, item) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        item = arguments?.get(ARGUMENT_KEY) as CalculatorItem
        setStyle(STYLE_NO_TITLE, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return DialogCalculatorItemBinding.inflate(inflater).apply { binding = this }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            calculatorItemDialogOriginalGradeValue.text =
                item.originalGrade ?: requireContext().getText(
                    R.string.all_no_data
                )

            val weightString = item.weight.round(2).toString()

            calculatorItemDialogColorAndWeightValue.text = weightString

            calculatorItemDialogTitle.text = item.title ?: requireContext().getText(
                R.string.all_no_data
            )

            calculatorItemDialogWeightValue.text = weightString

            calculatorItemDialogGradeValue.text = item.grade.round(2).toString()

            calculatorItemDialogClose.setOnClickListener { dismiss() }

            calculatorItemDialogDelete.setOnClickListener {
                calculator.deleteItem(item)
                dismiss()
            }
        }
    }
}
