package io.github.wulkanowy.ui.modules.note

import io.github.wulkanowy.data.Resource
import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.data.repositories.NoteRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.afterLoading
import io.github.wulkanowy.utils.flowWithResource
import io.github.wulkanowy.utils.flowWithResourceIn
import io.github.wulkanowy.utils.logStatus
import io.github.wulkanowy.utils.mapData
import io.github.wulkanowy.utils.onData
import io.github.wulkanowy.utils.onError
import io.github.wulkanowy.utils.onSuccess
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class NotePresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val noteRepository: NoteRepository,
    private val semesterRepository: SemesterRepository,
    private val analytics: AnalyticsHelper
) : BasePresenter<NoteView>(errorHandler, studentRepository) {

    private lateinit var lastError: Throwable

    override fun onAttachView(view: NoteView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Note view was initialized")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        loadData()
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the note")
        loadData(true)
    }

    fun onRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        loadData(true)
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }

    private fun loadData(forceRefresh: Boolean = false) {
        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            noteRepository.getNotes(student, semester, forceRefresh)
        }
            .logStatus("load note data")
            .onError(errorHandler::dispatch)
            .mapData {
                it.sortedByDescending { note -> note.date }
            }.onEach {
                view?.run {
                    enableSwipe(true)
                    showProgress(false)
                }
            }.onData {
                view?.run {
                    showRefresh(true)
                    showErrorView(false)
                    showContent(it.isNotEmpty())
                    showEmpty(it.isEmpty())
                    updateData(it)
                }
            }.afterLoading {
                view?.showRefresh(false)
            }.onSuccess {
                analytics.logEvent(
                    "load_data",
                    "type" to "note",
                    "items" to it.size
                )
            }.launch()
    }

    private fun showErrorViewOnError(message: String, error: Throwable) {
        view?.run {
            if (isViewEmpty) {
                lastError = error
                setErrorDetails(message)
                showErrorView(true)
                showEmpty(false)
            } else showError(message, error)
        }
    }

    fun onNoteItemSelected(note: Note, position: Int) {
        Timber.i("Select note item ${note.id}")
        view?.run {
            showNoteDialog(note)
            if (!note.isRead) {
                note.isRead = true
                updateItem(note, position)
                updateNote(note)
            }
        }
    }

    private fun updateNote(note: Note) {
        flowWithResource { noteRepository.updateNote(note) }
            .onEach {
                when (it) {
                    is Resource.Loading -> Timber.i("Attempt to update note ${note.id}")
                    is Resource.Success -> Timber.i("Update note result: Success")
                    is Resource.Error -> {
                        Timber.i("Update note result: An exception occurred")
                        errorHandler.dispatch(it.error)
                    }
                }
            }
            .launch("update_note")
    }
}
