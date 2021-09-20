package io.github.wulkanowy.ui.modules.homework.add

import io.github.wulkanowy.ui.base.BaseView
import java.time.LocalDate

interface HomeworkAddView : BaseView {

    fun initView()

    fun closeDialog()

    fun checkFields()

    fun showDatePickerDialog(currentDate: LocalDate)
}
