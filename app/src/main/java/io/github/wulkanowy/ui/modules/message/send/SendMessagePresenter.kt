package io.github.wulkanowy.ui.modules.message.send

import io.github.wulkanowy.api.messages.ReportingUnit
import io.github.wulkanowy.data.repositories.reportingunit.ReportingUnitRepository
import io.github.wulkanowy.data.repositories.message.MessageRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.session.BaseSessionPresenter
import io.github.wulkanowy.ui.base.session.SessionErrorHandler
import io.github.wulkanowy.utils.SchedulersProvider
import javax.inject.Inject

class SendMessagePresenter @Inject constructor(
    private val errorHandler: SessionErrorHandler,
    private val schedulers: SchedulersProvider,
    private val studentRepository: StudentRepository,
    private val messageRepository: MessageRepository,
    private val reportingUnitRepository: ReportingUnitRepository
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
            add(studentRepository.getCurrentStudent()
                .flatMap { reportingUnitRepository.getReportingUnits(it) }
                .subscribeOn(schedulers.backgroundThread)
                .observeOn(schedulers.mainThread)
                .doFinally {
                    view?.run {
                        showProgress(false)
                    }
                }
                .subscribe({
                    try {
                        if (it.isEmpty()) throw Exception("Couldn't fetch reporting units") // TODO Choose better exception class
                        view?.apply {
                            updateData(it)
                            showContent(true)
                        }
                        updateData(reportingUnits)
                    } catch (error: Throwable) {
                        errorHandler.dispatch(error)
                    }
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
