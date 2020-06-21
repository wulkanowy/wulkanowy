package io.github.wulkanowy.ui.modules.about.license

import com.mikepenz.aboutlibraries.entity.Library
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.DispatchersProvider
import io.github.wulkanowy.utils.SchedulersProvider
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LicensePresenter @Inject constructor(
    schedulers: SchedulersProvider,
    private val dispatchers: DispatchersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository
) : BasePresenter<LicenseView>(errorHandler, studentRepository, schedulers) {

    override fun onAttachView(view: LicenseView) {
        super.onAttachView(view)
        view.initView()
        loadData()
    }

    fun onItemSelected(library: Library) {
        view?.run { library.license?.licenseDescription?.let { openLicense(it) } }
    }

    private fun loadData() {
        launch {
            flow {
                emit(withContext(dispatchers.backgroundThread) {
                    view?.appLibraries.orEmpty()
                })
            }.onCompletion {
                view?.showProgress(false)
            }.catch {
                errorHandler.dispatch(it)
            }.collect {
                view?.updateData(it)
            }
        }
    }
}
