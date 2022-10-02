package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.MailboxType
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.MessageRepository
import io.github.wulkanowy.data.repositories.RecipientRepository
import javax.inject.Inject

class RecipientWork @Inject constructor(
    private val messageRepository: MessageRepository,
    private val recipientRepository: RecipientRepository
) : Work {

    override suspend fun doWork(student: Student, semester: Semester, notify: Boolean) {
        messageRepository.refreshMailboxes(student)

        val mailbox = messageRepository.getMailbox(student)

        if (mailbox != null) {
            recipientRepository.refreshRecipients(student, mailbox, MailboxType.EMPLOYEE)
        }
    }
}
