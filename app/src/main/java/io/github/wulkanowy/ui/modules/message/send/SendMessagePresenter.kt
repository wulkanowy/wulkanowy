package io.github.wulkanowy.ui.modules.message.send

import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.data.repositories.message.MessageRepository
import io.github.wulkanowy.data.repositories.recipient.RecipientRepository
import io.github.wulkanowy.data.repositories.reportingunit.ReportingUnitRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
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
    private val semesterRepository: SemesterRepository,
    private val messageRepository: MessageRepository,
    private val reportingUnitRepository: ReportingUnitRepository,
    private val recipientRepository: RecipientRepository
) : BaseSessionPresenter<SendMessageView>(errorHandler) {

    private lateinit var reportingUnit: ReportingUnit

    override fun onAttachView(view: SendMessageView) {
        super.onAttachView(view)
        view.initView()
        loadRecipients()
    }

    private fun loadRecipients() {
        disposable.add(studentRepository.getCurrentStudent()
            .flatMapMaybe { student ->
                semesterRepository.getCurrentSemester(student)
                    .flatMapMaybe { reportingUnitRepository.getReportingUnit(student, it.unitId) }
                    .doOnSuccess { reportingUnit = it }
                    .flatMap { recipientRepository.getRecipients(student, 2, it).toMaybe() }
            }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doFinally {
                view?.run {
                    showProgress(false)
                }
            }
            .subscribe({
                view?.apply {
                    setReportingUnit(reportingUnit)
                    showContent(true)
                }
                Timber.i("Fetched %s recipients", it.size.toString())
            }, {
                errorHandler.dispatch(it)
            }, {
                Timber.e("Couldn't fetch the reporting unit")
            })
        )
    }
}
