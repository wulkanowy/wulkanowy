package io.github.wulkanowy.ui.modules.account

import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import timber.log.Timber
import javax.inject.Inject

class AccountPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
) : BasePresenter<AccountView>(errorHandler, studentRepository) {

    fun onAttachView(view: AccountView, studentsWithSemesters: List<StudentWithSemesters>) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Account view was initialized")
        view.updateData(createAccountItems(studentsWithSemesters))
    }

    fun onAddSelected() {
        Timber.i("Select add account")
        view?.openLoginView()
    }

    fun onItemSelected(studentWithSemesters: StudentWithSemesters) {
        view?.openAccountDetailsView(studentWithSemesters)
    }

    private fun createAccountItems(items: List<StudentWithSemesters>): List<AccountItem<*>> {
        return items.groupBy {
            Account("${it.student.userName} (${it.student.email})", it.student.isParent)
        }
            .map { (account, students) ->
                listOf(
                    AccountItem(account, AccountItem.ViewType.HEADER)
                ) + students.map { student ->
                    AccountItem(student, AccountItem.ViewType.ITEM)
                }
            }
            .flatten()
    }
}
