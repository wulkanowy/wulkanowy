package io.github.wulkanowy.ui.modules.message.preview

import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.repositories.MessagesRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.logEvent
import javax.inject.Inject

class PreviewPresenter @Inject constructor(
    private val errorHandler: ErrorHandler,
    private val schedulers: SchedulersProvider,
    private val messagesRepository: MessagesRepository,
    private val studentRepository: StudentRepository
) : BasePresenter<PreviewView>(errorHandler) {

    var messageId: Long = 0

    fun onAttachView(view: PreviewView, id: Long) {
        super.onAttachView(view)
        loadData(id)
    }

    private fun loadData(id: Long) {
        messageId = id
        disposable.apply {
            clear()
            add(studentRepository.getCurrentStudent()
                .flatMap { messagesRepository.getMessage(it.studentId, id) }
                .subscribeOn(schedulers.backgroundThread)
                .observeOn(schedulers.mainThread)
                .doFinally {
                    view?.showProgress(false)
                }
                .subscribe({
                    view?.setData(it.first())
                    logEvent("Message load", mapOf("items" to it.size))
                }) {
                    errorHandler.dispatch(it)
                })
        }
    }
}
