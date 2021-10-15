package io.github.wulkanowy.ui.modules.account.accountedit

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.StudentNickAndAvatar
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.afterLoading
import io.github.wulkanowy.utils.flowWithResource
import io.github.wulkanowy.utils.logStatus
import io.github.wulkanowy.utils.onSuccess
import io.github.wulkanowy.utils.withErrorHandler
import timber.log.Timber
import javax.inject.Inject

class AccountEditPresenter @Inject constructor(
    appInfo: AppInfo,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository
) : BasePresenter<AccountEditView>(errorHandler, studentRepository) {

    lateinit var student: Student

    private val colors = appInfo.defaultColorsForAvatar.map { it.toInt() }

    fun onAttachView(view: AccountEditView, student: Student) {
        super.onAttachView(view)
        this.student = student

        with(view) {
            initView()
            showCurrentNick(student.nick.trim())
        }
        Timber.i("Account edit dialog view was initialized")
        loadData()

        view.updateColorsData(colors)
    }

    private fun loadData() {
        flowWithResource {
            studentRepository.getStudentById(student.id, false).avatarColor
        }.logStatus("load student").withErrorHandler(errorHandler).onSuccess {
            view?.updateSelectedColorData(it.toInt())
        }.launch("load_data")
    }

    fun changeStudentNickAndAvatar(nick: String, avatarColor: Int) {
        flowWithResource {
            val studentNick =
                StudentNickAndAvatar(nick = nick.trim(), avatarColor = avatarColor.toLong())
                    .apply { id = student.id }
            studentRepository.updateStudentNickAndAvatar(studentNick)
        }.logStatus("change student nick and avatar").withErrorHandler(errorHandler)
            .onSuccess {
                view?.recreateMainView()
            }
            .afterLoading { view?.popView() }
            .launch("update_student")
    }
}
