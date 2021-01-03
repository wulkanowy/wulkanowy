import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.dao.MessageAttachmentDao
import io.github.wulkanowy.data.db.dao.MessagesDao
import io.github.wulkanowy.data.db.entities.MessageWithAttachment
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.MessageRepository
import io.github.wulkanowy.getMessageEntity
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.pojo.MessageDetails
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.net.UnknownHostException

class MessageRepositoryTest {

    @MockK
    private lateinit var sdk: Sdk

    @MockK
    private lateinit var messageDb: MessagesDao

    @MockK
    private lateinit var messageAttachmentDao: MessageAttachmentDao

    @MockK
    lateinit var student: Student

    private lateinit var messageRepository: MessageRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { student.userName } returns "Jan"
        messageRepository = MessageRepository(messageDb, messageAttachmentDao, sdk)
    }

    @Test
    fun `throw error when message is not in the db`() {
        val testMessage = getMessageEntity(1, "", false)
        coEvery { messageDb.loadMessageWithAttachment(student.studentId, testMessage.messageId) } throws NullPointerException("No message in database")

        val message = runCatching { runBlocking { messageRepository.getMessage(student, testMessage).toList()[1] } }
        assertEquals(NullPointerException::class.java, message.exceptionOrNull()?.javaClass)
    }

    @Test
    fun `get message when content already in db`() {
        val testMessage = getMessageEntity(123, "Test", false)
        val messageWithAttachment = MessageWithAttachment(testMessage, emptyList())

        coEvery { messageDb.loadMessageWithAttachment(student.studentId, testMessage.messageId) } returns flowOf(messageWithAttachment)

        val message = runBlocking { messageRepository.getMessage(student, testMessage).toList() }

        assertEquals(Status.SUCCESS, message[1].status)
        assertEquals("Test", message[1].data!!.message.content)
    }

    @Test
    fun `get message when content in db is empty`() {
        val testMessage = getMessageEntity(123, "", true)
        val testMessageWithContent = testMessage.copy().apply { content = "Test" }

        val mWa = MessageWithAttachment(testMessage, emptyList())
        val mWaWithContent = MessageWithAttachment(testMessageWithContent, emptyList())

        coEvery { messageDb.loadMessageWithAttachment(student.studentId, testMessage.messageId) } returnsMany listOf(flowOf(mWa), flowOf(mWaWithContent))
        coEvery { sdk.getMessageDetails(testMessage.messageId, any(), any()) } returns MessageDetails("Test", emptyList())
        coEvery { messageDb.updateAll(any()) } just Runs
        coEvery { messageAttachmentDao.insertAttachments(any()) } returns listOf(1)

        val message = runBlocking { messageRepository.getMessage(student, testMessage).toList() }

        assertEquals(Status.SUCCESS, message[2].status)
        assertEquals("Test", message[2].data!!.message.content)
        coVerify { messageDb.updateAll(listOf(testMessageWithContent)) }
    }

    @Test
    fun `get message when content in db is empty and there is no internet connection`() {
        val testMessage = getMessageEntity(123, "", false)
        val messageWithAttachment = MessageWithAttachment(testMessage, emptyList())

        coEvery { messageDb.loadMessageWithAttachment(student.studentId, testMessage.messageId) } throws UnknownHostException()

        val message = runCatching { runBlocking { messageRepository.getMessage(student, testMessage).toList()[1] } }
        assertEquals(UnknownHostException::class.java, message.exceptionOrNull()?.javaClass)
    }

    @Test
    fun `get message when content in db is empty, unread and there is no internet connection`() {
        val testMessage = getMessageEntity(123, "", true)
        val messageWithAttachment = MessageWithAttachment(testMessage, emptyList())

        coEvery { messageDb.loadMessageWithAttachment(student.studentId, testMessage.messageId) } throws UnknownHostException()

        val message = runCatching { runBlocking { messageRepository.getMessage(student, testMessage).toList()[1] } }
        assertEquals(UnknownHostException::class.java, message.exceptionOrNull()?.javaClass)
    }
}
