package io.github.wulkanowy.ui.modules.account

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.services.sync.SyncManager
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.DispatchersProvider
import io.github.wulkanowy.utils.SchedulersProvider
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class AccountPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    private val dispatchers: DispatchersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val syncManager: SyncManager
) : BasePresenter<AccountView>(errorHandler, studentRepository, schedulers) {

    override fun onAttachView(view: AccountView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Account dialog view was initialized")
        loadData()
    }

    fun onAddSelected() {
        Timber.i("Select add account")
        view?.openLoginView()
    }

    fun onRemoveSelected() {
        Timber.i("Select remove account")
        view?.showConfirmDialog()
    }

    fun onLogoutConfirm() {
        Timber.i("Attempt to logout current user ")
        launch {
            flow {
                val student = studentRepository.getCurrentStudent(false)
                studentRepository.logoutStudent(student)

                val students = studentRepository.getSavedStudents(false)
                if (students.isNotEmpty()) {
                    studentRepository.switchStudent(students[0])
                }

                emit(students)
            }.onCompletion {
                view?.dismissView()
            }.catch {
                Timber.i("Logout result: An exception occurred")
                errorHandler.dispatch(it)
            }.collect {
                view?.apply {
                    if (it.isEmpty()) {
                        Timber.i("Logout result: Open login view")
                        syncManager.stopSyncWorker()
                        openClearLoginView()
                    } else {
                        Timber.i("Logout result: Switch to another student")
                        recreateMainView()
                    }
                }
            }
        }
    }

    fun onItemSelected(student: Student) {
        Timber.i("Select student item ${student.id}")
        if (student.isCurrent) {
            view?.dismissView()
        } else {
            Timber.i("Attempt to change a student")
            launch {
                flow { emit(studentRepository.switchStudent(student)) }
                    .onCompletion {
                        view?.dismissView()
                    }
                    .catch {
                        Timber.i("Change a student result: An exception occurred")
                        errorHandler.dispatch(it)
                    }
                    .collect {
                        Timber.i("Change a student result: Success")
                        view?.recreateMainView()
                    }
            }
        }
    }

    private fun createAccountItems(items: List<Student>): List<AccountItem<*>> {
        return items.groupBy { Account(it.email, it.isParent) }.map { (account, students) ->
            listOf(AccountItem(account, AccountItem.ViewType.HEADER)) + students.map { student ->
                AccountItem(student, AccountItem.ViewType.ITEM)
            }
        }.flatten()
    }

    private fun loadData() {
        Timber.i("Loading account data started")
        launch {
            flow { emit(studentRepository.getSavedStudents(false)) }
                .map { createAccountItems(it) }
                .catch {
                    Timber.i("Loading account result: An exception occurred")
                    errorHandler.dispatch(it)
                }
                .flowOn(dispatchers.backgroundThread)
                .collect {
                    println("A context with name: ${coroutineContext + CoroutineName("test")}")
                    Timber.i("Loading account result: Success")
                    view?.updateData(it)
                }
        }
    }
}
