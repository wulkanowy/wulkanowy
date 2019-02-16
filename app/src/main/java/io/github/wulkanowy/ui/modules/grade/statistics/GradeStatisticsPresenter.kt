package io.github.wulkanowy.ui.modules.grade.statistics

import io.github.wulkanowy.data.repositories.gradestatistics.GradeStatisticsRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.session.BaseSessionPresenter
import io.github.wulkanowy.ui.base.session.SessionErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import javax.inject.Inject

class GradeStatisticsPresenter @Inject constructor(
    private val errorHandler: SessionErrorHandler,
    private val gradeStatisticsRepository: GradeStatisticsRepository,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val schedulers: SchedulersProvider,
    private val analytics: FirebaseAnalyticsHelper
) : BaseSessionPresenter<GradeStatisticsView>(errorHandler) {

    override fun onAttachView(view: GradeStatisticsView) {
        super.onAttachView(view)
        view.initView()
    }
}
