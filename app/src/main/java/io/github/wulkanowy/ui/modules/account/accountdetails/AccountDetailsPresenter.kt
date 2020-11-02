package io.github.wulkanowy.ui.modules.account.accountdetails

import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import javax.inject.Inject

class AccountDetailsPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository
) : BasePresenter<AccountDetailsView>(errorHandler, studentRepository) {

    lateinit var studentWithSemesters: StudentWithSemesters

    override fun onAttachView(view: AccountDetailsView) {
        super.onAttachView(view)

        view.showAccountData(studentWithSemesters)
    }
}