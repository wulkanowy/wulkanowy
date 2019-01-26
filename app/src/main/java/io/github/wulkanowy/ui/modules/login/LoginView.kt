package io.github.wulkanowy.ui.modules.login

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.ui.base.BaseView

interface LoginView : BaseView {

    val currentViewIndex: Int

    fun initAdapter()

    fun hideActionBar()

    fun switchView(index: Int)

    fun notifyInitSymbolFragment(email: String, pass: String, endpoint: String)

    fun notifyInitStudentSelectFragment(students: List<Student>)
}
