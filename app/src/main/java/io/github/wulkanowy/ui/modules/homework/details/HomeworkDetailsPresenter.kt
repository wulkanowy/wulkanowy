package io.github.wulkanowy.ui.modules.homework.details

import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.repositories.HomeworkRepository
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.*
import timber.log.Timber
import javax.inject.Inject

class HomeworkDetailsPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val homeworkRepository: HomeworkRepository,
    private val analytics: AnalyticsHelper,
    private val preferencesRepository: PreferencesRepository
) : BasePresenter<HomeworkDetailsView>(errorHandler, studentRepository) {

    var isHomeworkFullscreen
        get() = preferencesRepository.isHomeworkFullscreen
        set(value) {
            preferencesRepository.isHomeworkFullscreen = value
        }

    override fun onAttachView(view: HomeworkDetailsView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Homework details view was initialized")
    }

    fun deleteHomework(homework: Homework) {
        flowWithResource { homeworkRepository.deleteHomework(homework) }
            .logStatus("homework delete")
            .onResourceError(errorHandler::dispatch)
            .onResourceSuccess {
                view?.run {
                    showMessage(homeworkDeleteSuccess)
                    closeDialog()
                }
            }.launch("delete")
    }

    fun toggleDone(homework: Homework) {
        flowWithResource { homeworkRepository.toggleDone(homework) }
            .logStatus("homework details update")
            .onResourceError(errorHandler::dispatch)
            .onResourceSuccess {
                view?.updateMarkAsDoneLabel(homework.isDone)
                analytics.logEvent("homework_mark_as_done")
            }
            .launch("toggle")
    }
}
