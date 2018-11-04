package io.github.wulkanowy.ui.modules.messages.message

import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.repositories.MessagesRepository
import io.github.wulkanowy.data.repositories.SessionRepository
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
    private val sessionRepository: SessionRepository,
    private val messagesRepository: MessagesRepository
) : BasePresenter<MessagesView>(errorHandler) {

    private var senderId = 0

    fun onAttachView(view: MessagesView, senderId: Int, senderName: String) {
        super.onAttachView(view)
        view.run {
            initAdapter()
            setActivityTitle(senderName)
        }

        this.senderId = senderId

        insertNumberOfMessages()
        loadData()
    }

    fun loadMore(start: Int) {
        loadData(start)
    }

    private fun loadData(start: Int = 0) {
        disposable.add(sessionRepository.getSemesters()
            .map { it.single { semester -> semester.current } }
            .flatMap { messagesRepository.getMessagesBySenderId(it, senderId, start) }
            .map { messages -> messages.map { getMappedMessage(it) } }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({
                view?.run {
                    addToEnd(it)
                }
            }, {
//                view?.run { showEmpty(isViewEmpty) }
                errorHandler.proceed(it)
            })
        )
    }

    private fun insertNumberOfMessages() {
        disposable.add(sessionRepository.getSemesters()
            .map { it.single { semester -> semester.current } }
            .flatMap { messagesRepository.getNumberOfMessages(it, senderId) }
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
            User(if (e.folderId != 1) e.senderID.toString() else "0", e.sender, e.sender)
        )
    }
}
