package io.github.wulkanowy.ui.modules.debug.notification

import io.github.wulkanowy.data.repositories.GradeRepository
import io.github.wulkanowy.data.repositories.HomeworkRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.services.sync.notifications.NewGradeNotification
import io.github.wulkanowy.services.sync.notifications.NewHomeworkNotification
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.flowWithResourceIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

class NotificationDebugPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val gradeRepository: GradeRepository,
    private val newGradeNotification: NewGradeNotification,
    private val homeworkRepository: HomeworkRepository,
    private val newHomeworkNotification: NewHomeworkNotification,
) : BasePresenter<NotificationDebugView>(errorHandler, studentRepository) {

    override fun onAttachView(view: NotificationDebugView) {
        super.onAttachView(view)
        Timber.i("Notification debug view was initialized")
        view.initView()
    }

    fun sendGradeNotifications(numberOf: Int) {
        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)

            gradeRepository.getGrades(student, semester, false)
        }.onEach {
            val (details, _) = it.data ?: return@onEach

            newGradeNotification.notifyDetails(details.take(numberOf))
        }.launch("grades")
    }

    fun sendHomeworkNotifications(numberOf: Int) {
        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)

            homeworkRepository.getHomework(
                student = student,
                semester = semester,
                start = LocalDate.now().minusDays(10),
                end = LocalDate.now(),
                forceRefresh = false
            )
        }.onEach {
            val items = it.data ?: return@onEach

            newHomeworkNotification.notify(items.take(numberOf))
        }.launch("homework")
    }
}
