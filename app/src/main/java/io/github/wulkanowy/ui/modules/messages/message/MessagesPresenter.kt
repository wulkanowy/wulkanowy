package io.github.wulkanowy.ui.modules.messages.message

import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.repositories.MessagesRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.messages.Message
import io.github.wulkanowy.ui.modules.messages.User
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.toDate
import javax.inject.Inject
import io.github.wulkanowy.data.db.entities.Message as MessageEntity

class MessagesPresenter @Inject constructor(
    private val errorHandler: ErrorHandler,
    private val schedulers: SchedulersProvider,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val messagesRepository: MessagesRepository
) : BasePresenter<MessagesView>(errorHandler) {

    private var conversationId = 0

    fun onAttachView(view: MessagesView, conversationId: Int, conversationName: String) {
        super.onAttachView(view)
        view.run {
            initAdapter()
            setActivityTitle(conversationName)
        }

        this.conversationId = conversationId

        insertNumberOfMessages()
        loadData()
    }

    fun loadMore(start: Int) {
        loadData(start)
    }

    private fun loadData(start: Int = 0) {
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getCurrentSemester(it) }
            .flatMap { messagesRepository.getMessagesByConversationId(it, conversationId, start, 2) }
            .map { messages -> messages.map { getMappedMessage(it) } }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doFinally { view?.showProgress(false) }
            .subscribe({
                view?.run {
                    showProgress(true)
                    addToEnd(it)
                }
            }, {
                errorHandler.proceed(it)
            })
        )
    }

    private fun insertNumberOfMessages() {
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getCurrentSemester(it) }
            .flatMap { messagesRepository.getNumberOfMessages(it, conversationId) }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({
                view?.setTotalMessages(it)
            }, { errorHandler.proceed(it) })
        )
    }

    private fun getMappedMessage(e: MessageEntity): Message {
        return Message(
            e.realId.toString(),
            (if (!e.subject.isNullOrEmpty()) "Temat: " + e.subject + "\n\n" else "") + e.content?.trim(),
            e.date.toDate(),
            User(if (e.folderId == 2) "0" else e.senderID.toString(), e.sender, null)
        )
    }
}
