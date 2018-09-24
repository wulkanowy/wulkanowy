package io.github.wulkanowy.ui.main.grade.details

import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.ui.base.BaseView

interface GradeDetailsView : BaseView {

    fun initView()

    fun updateData(data: List<GradeDetailsHeader>)

    fun resetView()

    fun clearView()

    fun showGradeDialog(grade: Grade)

    fun showContent(show: Boolean)

    fun showProgress(show: Boolean)

    fun showRefresh(show: Boolean)

    fun emptyAverageString(): String

    fun averageString(): String

    fun gradeNumberString(number: Int): String

    fun weightString(): String

    fun notifyParentDataLoaded(semesterId: String)

    fun notifyParentRefresh()
}
