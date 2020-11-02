package io.github.wulkanowy.ui.modules.account.accountdetails

import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.ui.base.BaseView

interface AccountDetailsView : BaseView {

    fun showAccountData(studentWithSemesters: StudentWithSemesters)
}