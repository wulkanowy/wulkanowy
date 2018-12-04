package io.github.wulkanowy.ui.modules.message.preview

import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.repositories.MessagesRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.logEvent
import io.github.wulkanowy.utils.toFormattedString
import javax.inject.Inject

class MessagePreviewPresenter @Inject constructor(
    private val errorHandler: ErrorHandler,
    private val schedulers: SchedulersProvider,
    private val messagesRepository: MessagesRepository,
    private val studentRepository: StudentRepository
) : BasePresenter<MessagePreviewView>(errorHandler) {

    var messageId: Long = 0

    fun onAttachView(view: MessagePreviewView, id: Long) {
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
                .subscribe({ messages ->
                    view?.run {
                        messages.first().let {
                            setSubject(if (it.subject.isNotBlank()) it.subject else noSubjectString)
                            setDate(it.date?.toFormattedString("yyyy-MM-dd HH:mm:ss"))
                            setContent(it.content)

                            if (it.recipient?.isNotBlank() == true) setRecipient(it.recipient)
                            else setSender(it.sender)
                        }
                    }
                    logEvent("Message load", mapOf("length" to messages.first().content?.length))
                }) {
                    errorHandler.dispatch(it)
                })
        }
    }
}
