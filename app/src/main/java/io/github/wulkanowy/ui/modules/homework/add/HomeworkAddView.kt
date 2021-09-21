package io.github.wulkanowy.ui.modules.homework.add

import io.github.wulkanowy.ui.base.BaseView
import java.time.LocalDate

interface HomeworkAddView : BaseView {

    val homeworkAddSuccess: String

    fun initView()

    fun checkFields()

    fun closeDialog()

    fun showDatePickerDialog(currentDate: LocalDate)
}
