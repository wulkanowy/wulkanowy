package io.github.wulkanowy.ui.modules.homework.add

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.repositories.HomeworkRepository
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.flowWithResource
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

class HomeworkAddPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val homeworkRepository: HomeworkRepository,
    private val semesterRepository: SemesterRepository,
    private val analytics: AnalyticsHelper,
    private val preferencesRepository: PreferencesRepository
) : BasePresenter<HomeworkAddView>(errorHandler, studentRepository) {

    var isHomeworkFullscreen
        get() = preferencesRepository.isHomeworkFullscreen
        set(value) {
            preferencesRepository.isHomeworkFullscreen = value
        }

    override fun onAttachView(view: HomeworkAddView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Homework details view was initialized")
    }

    fun onAddHomeworkClicked() {
        view?.checkFields()
    }

    fun showDatePicker(date: LocalDate?) {
        view?.showDatePickerDialog(date ?: LocalDate.now())
    }

    fun addHomework(
        subject: String,
        teacher: String,
        date: LocalDate,
        content: String
    ) {
        flowWithResource {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            val entryDate = LocalDate.now()
            homeworkRepository.insertHomework(
                listOf(
                    Homework(
                        semester.semesterId,
                        student.studentId,
                        date,
                        entryDate,
                        subject,
                        content,
                        teacher,
                        "",
                        emptyList(),
                    ).apply { isAddedByUser = true }
                )
            )
        }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.i("Homework insert start")
                Status.SUCCESS -> {
                    Timber.i("Homework insert: Success")
                    view?.run {
                        showMessage(homeworkAddSuccess)
                        closeDialog()
                    }
                }
                Status.ERROR -> {
                    Timber.i("Homework insert result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                }
            }
        }.launch("toggle")
    }
}
