package io.github.wulkanowy.ui.modules.messages.dialogs

import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.repositories.MessagesRepository
import io.github.wulkanowy.data.repositories.SessionRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.messages.Message
import io.github.wulkanowy.ui.modules.messages.User
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.toDate
import javax.inject.Inject

class DialogsPresenter @Inject constructor(
    val errorHandler: ErrorHandler,
    private val schedulers: SchedulersProvider,
    private val messagesRepository: MessagesRepository,
    private val sessionRepository: SessionRepository
) : BasePresenter<DialogsView>(errorHandler) {

    override fun onAttachView(view: DialogsView) {
        super.onAttachView(view)
        view.initView()
        loadData()
    }

    private fun loadData(forceRefresh: Boolean = false) {
        disposable.add(
            sessionRepository.getSemesters()
                .map { it.single { semester -> semester.current } }
                .flatMap { messagesRepository.getMessages(it, forceRefresh) }
                .map { messages -> messages.sortedByDescending { it.date } }
                .map { messages -> messages.groupBy { it.conversationId } }
                .map { messages ->
                    messages.map {
                        val dialogs = it.value.map { message ->
                            Message(message.realId.toString(), message.subject, message.date.toDate(),
                                User(message.conversationId.toString(), message.sender, null))
                        }
                        Dialog(it.key.toString(), null, it.value.first().sender,
                            arrayListOf(dialogs.first().user), dialogs.first(),
                            it.value.count { message -> message.unread ?: false }
                        )
                    }
                }
                .subscribeOn(schedulers.backgroundThread)
                .observeOn(schedulers.mainThread)
                .doOnSubscribe {
                    view?.run {
                        showProgress(!forceRefresh)
                        showEmpty(false)
                    }
                }
                .doFinally {
                    view?.run {
                        hideRefresh()
                        showProgress(false)
                    }
                }
                .subscribe({
                    view?.run {
                        updateData(it)
                    }
                }, {
                    view?.run { showEmpty(isViewEmpty) }
                    errorHandler.proceed(it)
                })
        )
    }

    fun onSwipeRefresh() {
        loadData(true)
    }

    fun onViewReselected() {
        loadData()
    }
}
