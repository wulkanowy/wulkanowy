package io.github.wulkanowy.ui.modules.login.studentselect

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import timber.log.Timber
import java.io.Serializable
import javax.inject.Inject

class LoginStudentSelectPresenter @Inject constructor(
    private val errorHandler: LoginErrorHandler,
    private val studentRepository: StudentRepository,
    private val schedulers: SchedulersProvider,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<LoginStudentSelectView>(errorHandler) {

    var students = emptyList<Student>()

    var selectedStudents = mutableListOf<Student>()

    fun onAttachView(view: LoginStudentSelectView, students: Serializable?) {
        super.onAttachView(view)
        view.run {
            initView()
            errorHandler.onStudentDuplicate = {
                showMessage(it)
                Timber.i("The student already registered in the app was selected")
            }
        }

        if (students is List<*> && students.isNotEmpty()) {
            loadData(students.filterIsInstance<Student>())
        }
    }

    fun onSignIn() {
        registerStudents()
    }

    fun onParentInitStudentSelectView(students: List<Student>) {
        loadData(students)
    }

    fun onItemSelected(item: AbstractFlexibleItem<*>?) {
        if (item is LoginStudentSelectItem) {
            selectedStudents.removeAll { it == item.student }
                .let { if (!it) selectedStudents.add(item.student) }
        }
    }

    private fun loadData(students: List<Student>) {
        this.students = students
        view?.apply {
            updateData(students.map { LoginStudentSelectItem(it) })
        }
    }

    private fun registerStudents() {
        disposable.add(studentRepository.saveStudents(selectedStudents)
            .flatMapCompletable {
                studentRepository.switchStudent(selectedStudents.first().apply { id = it.first() })
            }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doOnSubscribe {
                view?.apply {
                    showProgress(true)
                    showContent(false)
                }
                Timber.i("Registration started")
            }
            .subscribe({
                //analytics.logEvent("registration_student_select", SUCCESS to true, "endpoint" to student.endpoint, "symbol" to student.symbol, "error" to "No error")
                Timber.i("Registration result: Success")
                view?.openMainView()
            }, {
                //  analytics.logEvent("registration_student_select", SUCCESS to false, "endpoint" to student.endpoint, "symbol" to student.symbol, "error" to it.localizedMessage)
                Timber.i("Registration result: An exception occurred ")
                errorHandler.dispatch(it)
                view?.apply {
                    showProgress(false)
                    showContent(true)
                }
            }))
    }
}
