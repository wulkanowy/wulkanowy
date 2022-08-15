package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.MessageAttachment
import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.data.db.entities.Student
import java.time.Instant
import io.github.wulkanowy.sdk.pojo.Message as SdkMessage
import io.github.wulkanowy.sdk.pojo.MessageAttachment as SdkMessageAttachment
import io.github.wulkanowy.sdk.pojo.Recipient as SdkRecipient

fun List<SdkMessage>.mapToEntities(student: Student) = map {
    Message(
        studentId = student.id,
        realId = it.id ?: 0,
        messageId = it.messageId!!,
        sender = it.correspondents,
        senderId = it.sender?.loginId ?: 0,
        recipient = it.recipients.singleOrNull()?.name ?: "Wielu adresatów",
        subject = it.subject.trim(),
        date = it.dateZoned?.toInstant() ?: Instant.now(),
        folderId = it.folderId,
        unread = it.unread ?: false,
        removed = false, //todo
        hasAttachments = it.hasAttachments
    ).apply {
        content = it.content.orEmpty()
//        unreadBy = it.unreadBy ?: 0
//        readBy = it.readBy ?: 0
    }
}

fun List<SdkMessageAttachment>.mapToEntities() = map {
    MessageAttachment(
        realId = it.url.hashCode(),
        messageId = 0,//it.messageId,
        oneDriveId = "",
        url = it.url,
        filename = it.filename
    )
}

fun List<Recipient>.mapFromEntities() = map {
    SdkRecipient(
        name = it.realName,
        mailboxGlobalKey = it.hash,
        studentName = "",
        schoolNameShort = "",
    )
}
