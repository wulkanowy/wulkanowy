package io.github.wulkanowy.ui.modules.about.logviewer

import io.github.wulkanowy.data.repositories.logger.LoggerRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.SchedulersProvider
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class LogViewerPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val loggerRepository: LoggerRepository
) : BasePresenter<LogViewerView>(errorHandler, studentRepository, schedulers) {

    override fun onAttachView(view: LogViewerView) {
        super.onAttachView(view)
        view.initView()
        loadLogFile()
    }

    fun onShareLogsSelected(): Boolean {
        launch {
            flowOf(loggerRepository.getLogFiles())
                .catch {
                    Timber.i("Loading logs files result: An exception occurred")
                    errorHandler.dispatch(it)
                }
                .collect { files ->
                    Timber.i("Loading logs files result: ${files.joinToString { it.name }}")
                    view?.shareLogs(files)
                }
        }
        return true
    }

    fun onRefreshClick() {
        loadLogFile()
    }

    private fun loadLogFile() {
        launch {
            flowOf(loggerRepository.getLastLogLines())
                .catch {
                    Timber.i("Loading last log file result: An exception occurred")
                    errorHandler.dispatch(it)
                }
                .collect {
                    Timber.i("Loading last log file result: load ${it.size} lines")
                    view?.setLines(it)
                }
        }
    }
}
