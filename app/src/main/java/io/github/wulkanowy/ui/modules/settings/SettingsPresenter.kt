package io.github.wulkanowy.ui.modules.settings

import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import javax.inject.Inject

class SettingsPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
) : BasePresenter<SettingsView>(errorHandler, studentRepository) {}
