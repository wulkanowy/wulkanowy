package io.github.wulkanowy.ui.modules.login.studentselect

import io.github.wulkanowy.TestSchedulersProvider
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDateTime.now

class LoginStudentSelectPresenterTest {

    @MockK
    lateinit var errorHandler: LoginErrorHandler

    @MockK
    lateinit var loginStudentSelectView: LoginStudentSelectView

    @MockK
    lateinit var studentRepository: StudentRepository

    @MockK
    lateinit var analytics: FirebaseAnalyticsHelper

    private lateinit var presenter: LoginStudentSelectPresenter

    private val testStudent by lazy { Student(email = "test", password = "test123", scrapperBaseUrl = "https://fakelog.cf", loginType = "AUTO", symbol = "", isCurrent = false, studentId = 0, schoolName = "", schoolSymbol = "", classId = 1, studentName = "", registrationDate = now(), className = "", loginMode = "", certificateKey = "", privateKey = "", mobileBaseUrl = "", schoolShortName = "", userLoginId = 1, isParent = false) }

    private val testException by lazy { RuntimeException("Problem") }

    @Before
    fun initPresenter() {
        MockKAnnotations.init(this)
        presenter = LoginStudentSelectPresenter(TestSchedulersProvider(), studentRepository, errorHandler, analytics)
        presenter.onAttachView(loginStudentSelectView, null)
    }

    @Test
    fun initViewTest() {
        verify { loginStudentSelectView.initView() }
    }

    @Test
    fun onSelectedStudentTest() {
        coEvery { studentRepository.saveStudents(listOf(testStudent)) } returns listOf(1L)
        coEvery { studentRepository.switchStudent(testStudent) }
        presenter.onItemSelected(testStudent, false)
        presenter.onSignIn()

        verify { loginStudentSelectView.showContent(false) }
        verify { loginStudentSelectView.showProgress(true) }
        verify { loginStudentSelectView.openMainView() }
    }

    @Test
    fun onSelectedStudentErrorTest() {
        coEvery { studentRepository.saveStudents(listOf(testStudent)) } throws testException
        coEvery { studentRepository.logoutStudent(testStudent) }
        presenter.onItemSelected(testStudent, false)
        presenter.onSignIn()
        verify { loginStudentSelectView.showContent(false) }
        verify { loginStudentSelectView.showProgress(true) }
        verify { errorHandler.dispatch(testException) }
    }
}
