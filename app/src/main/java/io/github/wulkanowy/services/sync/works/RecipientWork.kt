package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.repositories.recipient.RecipientRepository
import io.github.wulkanowy.data.repositories.reportingunit.ReportingUnitRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.reactivex.Completable
import javax.inject.Inject

class RecipientWork @Inject constructor(
    private val studentRepository: StudentRepository,
    private val reportingUnitRepository: ReportingUnitRepository,
    private val recipientRepository: RecipientRepository
) : Work {

    override fun create(): Completable {
        return studentRepository.getCurrentStudent()
            .flatMapCompletable { student ->
                reportingUnitRepository.getReportingUnits(student)
                    .flatMapCompletable { units ->
                        Completable.mergeDelayError(units.map {
                            recipientRepository.getRecipients(student, 2, it).ignoreElement()
                        })
                    }
            }
    }
}
