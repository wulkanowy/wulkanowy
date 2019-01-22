package io.github.wulkanowy.ui.modules.luckynumber

import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.SchedulersProvider
import io.reactivex.Single
import kotlinx.android.synthetic.main.dialog_lucky_number.*
import javax.inject.Inject

class LuckyNumberSettingsPresenter @Inject constructor(
    private val errorHandler: ErrorHandler,
    private val schedulers: SchedulersProvider,
    private val studentRepository: StudentRepository
) : BasePresenter<LuckyNumberSettingsDialog>(errorHandler) {

    override fun onAttachView(view: LuckyNumberSettingsDialog) {
        super.onAttachView(view)

        view.initView()
        loadData()
    }

    private fun loadData() {
        disposable.apply {
            clear()
            add(studentRepository.getCurrentStudent()
                .subscribeOn(schedulers.backgroundThread)
                .observeOn(schedulers.mainThread)
                .subscribe({
                    view?.apply {
                        updateData(
                            it.luckyNumberAllNotifications,
                            it.luckyNumberSelfNotifications,
                            it.registerNumber
                        )
                    }
                }, {
                    errorHandler.dispatch(it)
                })
            )
        }
    }

    fun onSave() {
        disposable.apply {
            add(studentRepository.getCurrentStudent()
                .subscribeOn(schedulers.backgroundThread)
                .observeOn(schedulers.mainThread)
                .subscribe({
                    view?.run {
                        val allNotifications = luckyNumberSwitchAllNotifications.isChecked
                        val selfNotifications = luckyNumberSwitchSelfNotifications.isChecked
                        val registerNumber = luckyNumberInputRegisterNumber.text.toString().toIntOrNull()

                        studentRepository.updateLuckyNumberSettings(it, allNotifications, selfNotifications, registerNumber)
                            .subscribeOn(schedulers.backgroundThread)
                            .observeOn(schedulers.mainThread)
                            .subscribe({
                                dismiss()
                            }, { it -> Single.error<Throwable>(it) })
                    }
                }, { errorHandler.dispatch(it) }))
        }
    }
}
