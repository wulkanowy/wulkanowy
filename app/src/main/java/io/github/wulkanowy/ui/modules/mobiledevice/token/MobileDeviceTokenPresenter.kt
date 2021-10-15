package io.github.wulkanowy.ui.modules.mobiledevice.token

import io.github.wulkanowy.data.Resource
import io.github.wulkanowy.data.repositories.MobileDeviceRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.afterLoading
import io.github.wulkanowy.utils.flowWithResource
import io.github.wulkanowy.utils.logStatus
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class MobileDeviceTokenPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val mobileDeviceRepository: MobileDeviceRepository,
    private val analytics: AnalyticsHelper
) : BasePresenter<MobileDeviceTokenVIew>(errorHandler, studentRepository) {

    override fun onAttachView(view: MobileDeviceTokenVIew) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Mobile device view was initialized")
        loadData()
    }

    private fun loadData() {
        flowWithResource {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            mobileDeviceRepository.getToken(student, semester)
        }.logStatus("load mobile device registration").onEach {
            when (it) {
                is Resource.Success -> {
                    view?.run {
                        updateData(it.data)
                        showContent()
                    }
                    analytics.logEvent(
                        "device_register",
                        "symbol" to it.data.token.substring(0, 3)
                    )
                }
                is Resource.Error -> {
                    view?.closeDialog()
                    errorHandler.dispatch(it.error)
                }
            }
        }.afterLoading {
            view?.hideLoading()
        }.launch()
    }
}
