package io.github.wulkanowy.ui.modules.homework.add

import io.github.wulkanowy.ui.base.BaseView
import java.time.LocalDate

interface HomeworkAddView : BaseView {

    fun initView()

    fun onAddClicked()

    fun showSuccessMessage()

    fun setErrorSubjectRequired()

    fun setErrorTeacherRequired()

    fun setErrorDateRequired()

    fun setErrorContentRequired()

    fun clearErrors()

    fun closeDialog()

    fun showDatePickerDialog(currentDate: LocalDate)
}
