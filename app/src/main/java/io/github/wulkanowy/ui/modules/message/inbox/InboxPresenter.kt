package io.github.wulkanowy.ui.modules.message.inbox

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.repositories.MessagesRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.message.MessageItem
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.logEvent
import javax.inject.Inject

class InboxPresenter @Inject constructor(
    private val errorHandler: ErrorHandler,
    private val schedulers: SchedulersProvider,
    private val messagesRepository: MessagesRepository,
    private val studentRepository: StudentRepository
) : BasePresenter<InboxView>(errorHandler) {

    override fun onAttachView(view: InboxView) {
        super.onAttachView(view)
        view.initView()
    }

    fun onSwipeRefresh() {
        onParentViewLoadData(true)
    }

    fun onParentViewLoadData(forceRefresh: Boolean) {
        disposable.apply {
            clear()
            add(studentRepository.getCurrentStudent()
                .flatMap { messagesRepository.getReceivedMessages(it.studentId, forceRefresh) }
                .map { items -> items.map { MessageItem(it) } }
                .subscribeOn(schedulers.backgroundThread)
                .observeOn(schedulers.mainThread)
                .doFinally {
                    view?.run {
                        showRefresh(false)
                        showProgress(false)
                        notifyParentDataLoaded()
                    }
                }
                .subscribe({
                    view?.run {
                        showEmpty(it.isEmpty())
                        showContent(it.isNotEmpty())
                        updateData(it)
                    }
                    logEvent("Message inbox load", mapOf("items" to it.size, "forceRefresh" to forceRefresh))
                }) {
                    view?.run { showEmpty(isViewEmpty) }
                    errorHandler.dispatch(it)
                })
        }
    }

    fun onMessageItemSelected(item: AbstractFlexibleItem<*>) {
        view?.openMessage((item as MessageItem).message.id)
    }
}
