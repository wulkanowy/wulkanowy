package io.github.wulkanowy.ui.modules.message.send

import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.data.repositories.message.MessageRepository
import io.github.wulkanowy.data.repositories.recipient.RecipientRepository
import io.github.wulkanowy.data.repositories.reportingunit.ReportingUnitRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.session.BaseSessionPresenter
import io.github.wulkanowy.ui.base.session.SessionErrorHandler
import io.github.wulkanowy.utils.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject

class SendMessagePresenter @Inject constructor(
    private val errorHandler: SessionErrorHandler,
    private val schedulers: SchedulersProvider,
    private val studentRepository: StudentRepository,
    private val messageRepository: MessageRepository,
    private val reportingUnitRepository: ReportingUnitRepository,
    private val recipientRepository: RecipientRepository
) : BaseSessionPresenter<SendMessageView>(errorHandler) {

    private var reportingUnits: List<ReportingUnit> = emptyList()

    override fun onAttachView(view: SendMessageView) {
        super.onAttachView(view)
        view.initView()
        loadReportingUnits()
    }

    private fun loadReportingUnits() {
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { reportingUnitRepository.getReportingUnits(it) }
            .filter { !it.isEmpty() }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({
                setReportingUnits(it)
                view?.setReportingUnit(it.first())
                loadRecipients(it.first())
            }, {
                errorHandler.dispatch(it)
            }, {
                view?.showProgress(false)
                errorHandler.dispatch(Exception("Cannot fetch reporting units"))
            })
        )
    }

    private fun loadRecipients(unit: ReportingUnit) {
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { recipientRepository.getRecipients(it, 2, unit) }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doFinally {
                view?.run {
                    showProgress(false)
                }
            }
            .subscribe({
                view?.apply {
                    showContent(true)
                }
                Timber.i("Fetched " + it.size.toString() + " recipients")
            }, {
                errorHandler.dispatch(it)
            })
        )
    }

    private fun setReportingUnits(reportingUnits: List<ReportingUnit>) {
        this.reportingUnits = reportingUnits
    }
}
