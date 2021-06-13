package io.github.wulkanowy.ui.modules.debug.notification

import io.github.wulkanowy.R
import io.github.wulkanowy.data.repositories.ConferenceRepository
import io.github.wulkanowy.data.repositories.ExamRepository
import io.github.wulkanowy.data.repositories.GradeRepository
import io.github.wulkanowy.data.repositories.HomeworkRepository
import io.github.wulkanowy.data.repositories.MessageRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.services.sync.notifications.NewConferenceNotification
import io.github.wulkanowy.services.sync.notifications.NewExamNotification
import io.github.wulkanowy.services.sync.notifications.NewGradeNotification
import io.github.wulkanowy.services.sync.notifications.NewHomeworkNotification
import io.github.wulkanowy.services.sync.notifications.NewMessageNotification
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
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
    private val conferenceRepository: ConferenceRepository,
    private val newConferenceNotification: NewConferenceNotification,
    private val examRepository: ExamRepository,
    private val newExamNotification: NewExamNotification,
    private val messageRepository: MessageRepository,
    private val newMessageNotification: NewMessageNotification,
) : BasePresenter<NotificationDebugView>(errorHandler, studentRepository) {

    private val items = listOf(
        NotificationDebugItem(R.string.grade_title) { sendGradeNotifications(it) },
        NotificationDebugItem(R.string.grade_summary_predicted_grade) {
            sendGradePredictedNotifications(it)
        },
        NotificationDebugItem(R.string.grade_summary_final_grade) { sendGradeFinalNotifications(it) },
        NotificationDebugItem(R.string.homework_title) { sendHomeworkNotifications(it) },
        NotificationDebugItem(R.string.conferences_title) { sendConferenceNotifications(it) },
        NotificationDebugItem(R.string.exam_title) { sendExamNotifications(it) },
        NotificationDebugItem(R.string.message_title) { sendMessageNotifications(it) },
    )

    override fun onAttachView(view: NotificationDebugView) {
        super.onAttachView(view)
        Timber.i("Notification debug view was initialized")
        with(view) {
            initView()
            setItems(items)
        }
    }

    fun sendGradeNotifications(numberOf: Int) {
        flow {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            val items = gradeRepository.getGradesFromDatabase(semester)

            emitAll(items)
        }.onEach {
            newGradeNotification.notifyDetails(it.take(numberOf))
        }.launch("grades")
    }

    fun sendGradePredictedNotifications(numberOf: Int) {
        flow {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            val items = gradeRepository.getGradesPredictedFromDatabase(semester)

            emitAll(items)
        }.onEach { items ->
            newGradeNotification.notifyPredicted(items
                .filter { it.predictedGrade.isNotEmpty() }
                .take(numberOf)
            )
        }.launch("grades_predicted")
    }

    fun sendGradeFinalNotifications(numberOf: Int) {
        flow {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            val items = gradeRepository.getGradesFinalFromDatabase(semester)

            emitAll(items)
        }.onEach { items ->
            newGradeNotification.notifyFinal(items
                .filter { it.finalGrade.isNotEmpty() }
                .take(numberOf)
            )
        }.launch("grades_final")
    }

    fun sendHomeworkNotifications(numberOf: Int) {
        flow {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            val items = homeworkRepository.getHomeworkFromDatabase(
                semester = semester,
                start = LocalDate.now().minusMonths(6),
                end = LocalDate.now(),
            )
            emitAll(items)
        }.onEach {
            newHomeworkNotification.notify(it.take(numberOf))
        }.launch("homework")
    }

    fun sendConferenceNotifications(numberOf: Int) {
        flow {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            val items = conferenceRepository.getConferenceFromDatabase(semester)
            emitAll(items)
        }.onEach {
            newConferenceNotification.notify(it.take(numberOf))
        }.launch("conference")
    }

    fun sendExamNotifications(numberOf: Int) {
        flow {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            val items = examRepository.getExamsFromDatabase(
                semester = semester,
                start = LocalDate.now().minusMonths(1),
            )
            emitAll(items)
        }.onEach {
            newExamNotification.notify(it.take(numberOf))
        }.launch("exam")
    }

    fun sendMessageNotifications(numberOf: Int) {
        flow {
            val student = studentRepository.getCurrentStudent()
            val items = messageRepository.getMessagesFromDatabase(student)
            emitAll(items)
        }.onEach {
            newMessageNotification.notify(it.take(numberOf))
        }.launch("message")
    }
}
