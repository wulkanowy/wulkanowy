package io.github.wulkanowy.ui.modules.note

import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.data.repositories.note.NoteRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class NotePresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val noteRepository: NoteRepository,
    private val semesterRepository: SemesterRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<NoteView>(errorHandler, studentRepository, schedulers) {

    private lateinit var lastError: Throwable

    private var refreshJob: Job? = null

    private var loadingJob: Job? = null

    override fun onAttachView(view: NoteView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Note view was initialized")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        loadData()
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the note")
        refreshData()
    }

    fun onRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        refreshData()
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }

    private fun refreshData() {
        refreshJob?.cancel()
        refreshJob = launch {
            flow {
                val student = studentRepository.getCurrentStudent()
                val semester = semesterRepository.getCurrentSemester(student)
                emit(noteRepository.refreshNotes(student, semester))
            }.onCompletion { afterLoading() }.catch { handleError(it) }.collect()
        }
    }

    private fun loadData() {
        Timber.i("Loading note data started")

        loadingJob?.cancel()
        loadingJob = launch {
            flow {
                val student = studentRepository.getCurrentStudent()
                val semester = semesterRepository.getCurrentSemester(student)
                emitAll(noteRepository.getNotes(student, semester))
            }.map { items ->
                items.sortedByDescending { it.date }
            }.onEach {
                afterLoading()
            }.catch {
                handleError(it)
            }.collect {
                Timber.i("Loading note result: Success")
                view?.apply {
                    updateData(it)
                    showEmpty(it.isEmpty())
                    showErrorView(false)
                    showContent(it.isNotEmpty())
                }
                analytics.logEvent(
                    "load_data",
                    "type" to "note",
                    "items" to it.size
                )
            }
        }
    }

    private fun afterLoading() {
        view?.run {
            hideRefresh()
            showProgress(false)
            enableSwipe(true)
        }
    }

    private fun handleError(error: Throwable) {
        Timber.i("Loading note result: An exception occurred")
        errorHandler.dispatch(error)
        afterLoading()
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
        Timber.i("Attempt to update note ${note.id}")
        launch {
            flow { emit(noteRepository.updateNote(note)) }.catch {
                Timber.i("Update note result: An exception occurred")
                errorHandler.dispatch(it)
            }.collect { Timber.i("Update note result: Success") }
        }
    }
}
