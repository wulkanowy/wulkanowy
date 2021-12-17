package io.github.wulkanowy.ui.modules.timetable.additional.add

import io.github.wulkanowy.ui.base.BaseView
import java.time.LocalDate
import java.time.LocalTime

interface AdditionalLessonAddView : BaseView {

    fun initView()

    fun closeDialog()

    fun showDatePickerDialog(defaultDate: LocalDate)

    fun showStartTimePickerDialog(defaultTime: LocalTime)

    fun showEndTimePickerDialog(defaultTime: LocalTime)

    fun showSuccessMessage()

    fun setErrorDateRequired()

    fun setErrorStartRequired()

    fun setErrorEndRequired()

    fun setErrorContentRequired()
}
