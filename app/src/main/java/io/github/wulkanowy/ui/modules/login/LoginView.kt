package io.github.wulkanowy.ui.modules.login

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.ui.base.BaseView

interface LoginView : BaseView {

    val currentViewIndex: Int

    fun initAdapter()

    fun switchView(index: Int)

    fun hideActionBar()

    fun notifyInitSymbolFragment(email: String, pass: String, endpoint: String)

    fun notifyInitStudentSelectFragment(students: List<Student>)
}
