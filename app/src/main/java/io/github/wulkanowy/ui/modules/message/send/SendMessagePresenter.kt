package io.github.wulkanowy.ui.modules.message.send

import io.github.wulkanowy.api.messages.ReportingUnit
import io.github.wulkanowy.data.repositories.MessagesRepository
import io.github.wulkanowy.ui.base.session.BaseSessionPresenter
import io.github.wulkanowy.ui.base.session.SessionErrorHandler
import io.github.wulkanowy.utils.SchedulersProvider
import javax.inject.Inject

class SendMessagePresenter @Inject constructor(
    private val errorHandler: SessionErrorHandler,
    private val schedulers: SchedulersProvider,
    private val messagesRepository: MessagesRepository
) : BaseSessionPresenter<SendMessageView>(errorHandler) {

    private var reportingUnits: List<ReportingUnit> = emptyList()

    override fun onAttachView(view: SendMessageView) {
        super.onAttachView(view)
        view.initView()
        loadData()
    }

    private fun loadData() {
        disposable.apply {
            clear()
            add(messagesRepository.getReportingUnits()
                .subscribeOn(schedulers.backgroundThread)
                .observeOn(schedulers.mainThread)
                .doFinally {
                    view?.run {
                        showProgress(false)
                    }
                }
                .subscribe({
                    if (it.isEmpty()) throw Exception("Couldn't fetch reporting units") // TODO Choose better exception class
                    view?.apply {
                        updateData(it)
                        showContent(true)
                    }
                    updateData(reportingUnits)
                }, {
                    errorHandler.dispatch(it)
                })
            )
        }
    }

    private fun updateData(reportingUnits: List<ReportingUnit>) {
        this.reportingUnits = reportingUnits
    }

}
