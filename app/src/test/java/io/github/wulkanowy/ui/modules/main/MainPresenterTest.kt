package io.github.wulkanowy.ui.modules.main

import io.github.wulkanowy.TestSchedulersProvider
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.services.sync.SyncManager
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class MainPresenterTest {

    @MockK
    lateinit var errorHandler: ErrorHandler

    @MockK
    lateinit var studentRepository: StudentRepository

    @MockK
    lateinit var prefRepository: PreferencesRepository

    @MockK
    lateinit var syncManager: SyncManager

    @MockK
    lateinit var mainView: MainView

    @MockK
    lateinit var analytics: FirebaseAnalyticsHelper

    private lateinit var presenter: MainPresenter

    @Before
    fun initPresenter() {
        MockKAnnotations.init(this)
        clearMocks(mainView)

        presenter = MainPresenter(TestSchedulersProvider(), errorHandler, studentRepository, prefRepository, syncManager, analytics)
        presenter.onAttachView(mainView, null)
    }

    @Test
    fun initMenuTest() {
        verify { mainView.initView() }
    }

    @Test
    fun onTabSelectedTest() {
        every { mainView.switchMenuView(1) } just Runs
        presenter.onTabSelected(1, false)
        verify { mainView.switchMenuView(1) }
    }
}

