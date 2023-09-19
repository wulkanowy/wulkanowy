package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.api.SchoolsService
import io.github.wulkanowy.data.api.ServerCommand
import io.github.wulkanowy.ui.modules.login.studentselect.LoginStudentSelectItem
import io.github.wulkanowy.utils.IntegrityHelper
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SchoolsRepository @Inject constructor(
    private val integrityHelper: IntegrityHelper,
    private val schoolsService: SchoolsService,
) {

    suspend fun logSchoolLogin(filteredStudents: List<LoginStudentSelectItem.Student>) {
        val data = filteredStudents.map {
            it.unit to it.symbol
        }

        data.forEach { (unit, symbol) ->
            unit.schoolName
            unit.schoolId
            symbol.symbol
            runCatching { logLogin() }
                .onFailure { Timber.e(it) }
        }
    }

    private suspend fun logLogin() {
        val token = integrityHelper.getIntegrityToken() ?: return

        schoolsService.performCommand(
            ServerCommand(
                commandString = "TRANSFER FROM alice TO bob CURRENCY gems QUANTITY 1000",
                tokenString = token,
            )
        )
    }
}
