package io.github.wulkanowy.ui.modules.login.studentselect

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.utils.DispatchersProvider
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.ifNullOrBlank
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.Serializable
import javax.inject.Inject

class LoginStudentSelectPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    private val dispatchers: DispatchersProvider,
    studentRepository: StudentRepository,
    private val loginErrorHandler: LoginErrorHandler,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<LoginStudentSelectView>(loginErrorHandler, studentRepository, schedulers) {

    private var lastError: Throwable? = null

    var students = emptyList<Student>()

    private val selectedStudents = mutableListOf<Student>()

    fun onAttachView(view: LoginStudentSelectView, students: Serializable?) {
        super.onAttachView(view)
        view.run {
            initView()
            showContact(false)
            enableSignIn(false)
            loginErrorHandler.onStudentDuplicate = {
                showMessage(it)
                Timber.i("The student already registered in the app was selected")
            }
        }

        if (students is List<*> && students.isNotEmpty()) {
            loadData(students.filterIsInstance<Student>())
        }
    }

    fun onSignIn() {
        registerStudents(selectedStudents)
    }

    fun onParentInitStudentSelectView(students: List<Student>) {
        loadData(students)
        if (students.size == 1) registerStudents(students)
    }

    fun onItemSelected(student: Student, alreadySaved: Boolean) {
        if (alreadySaved) return

        selectedStudents
            .removeAll { it == student }
            .let { if (!it) selectedStudents.add(student) }

        view?.enableSignIn(selectedStudents.isNotEmpty())
    }

    private fun compareStudents(a: Student, b: Student): Boolean {
        return a.email == b.email
            && a.symbol == b.symbol
            && a.studentId == b.studentId
            && a.schoolSymbol == b.schoolSymbol
            && a.classId == b.classId
    }

    private fun loadData(students: List<Student>) {
        resetSelectedState()
        this.students = students
        launch {
            flowOf(studentRepository.getSavedStudents(false))
                .map { savedStudents ->
                    students.map { student ->
                        student to savedStudents.any { compareStudents(student, it) }
                    }
                }
                .catch {
                    errorHandler.dispatch(it)
                    lastError = it
                    view?.updateData(students.map { student -> student to false })
                }
                .collect { view?.updateData(it) }
        }
    }

    private fun resetSelectedState() {
        selectedStudents.clear()
        view?.enableSignIn(false)
    }

    private fun registerStudents(students: List<Student>) {
        launch {
            flow { emit(studentRepository.saveStudents(students)) }
                .map { students.first().apply { id = it.first() } }
                .flatMapConcat { flowOf(studentRepository.switchStudent(it)) }
                .onStart {
                    view?.apply {
                        showProgress(true)
                        showContent(false)
                    }
                    Timber.i("Registration started")
                }
                .flowOn(dispatchers.backgroundThread)
                .catch { error ->
                    students.forEach { analytics.logEvent("registration_student_select", "success" to false, "scrapperBaseUrl" to it.scrapperBaseUrl, "symbol" to it.symbol, "error" to error.message.ifNullOrBlank { "No message" }) }
                    Timber.i("Registration result: An exception occurred ")
                    loginErrorHandler.dispatch(error)
                    lastError = error
                    view?.apply {
                        showProgress(false)
                        showContent(true)
                        showContact(true)
                    }
                }
                .collect {
                    students.forEach { analytics.logEvent("registration_student_select", "success" to true, "scrapperBaseUrl" to it.scrapperBaseUrl, "symbol" to it.symbol, "error" to "No error") }
                    Timber.i("Registration result: Success")
                    view?.openMainView()
                }
        }
    }

    fun onDiscordClick() {
        view?.openDiscordInvite()
    }

    fun onEmailClick() {
        view?.openEmail(lastError?.message.ifNullOrBlank { "empty" })
    }
}
