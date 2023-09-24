package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.api.IntegrityRequest
import io.github.wulkanowy.data.api.LoginEvent
import io.github.wulkanowy.data.api.SchoolsService
import io.github.wulkanowy.data.mappers.mapToStudentWithSemesters
import io.github.wulkanowy.data.pojos.RegisterSymbol
import io.github.wulkanowy.data.pojos.RegisterUnit
import io.github.wulkanowy.data.pojos.RegisterUser
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.ui.modules.login.LoginData
import io.github.wulkanowy.ui.modules.login.studentselect.LoginStudentSelectItem
import io.github.wulkanowy.utils.IntegrityHelper
import io.github.wulkanowy.utils.init
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Singleton
class SchoolsRepository @Inject constructor(
    private val integrityHelper: IntegrityHelper,
    private val schoolsService: SchoolsService,
    private val sdk: Sdk,
) {

    suspend fun logSchoolLogin(
        loginData: LoginData,
        registerUser: RegisterUser,
        filteredStudents: List<LoginStudentSelectItem.Student>
    ) {
        filteredStudents
            .map { it.symbol to it.unit }
            .forEach { (symbol, unit) ->
                runCatching { logLogin(loginData, registerUser, symbol, unit) }
                    .onFailure { Timber.e(it) }
            }
    }

    private suspend fun logLogin(
        loginData: LoginData,
        registerUser: RegisterUser,
        symbol: RegisterSymbol,
        unit: RegisterUnit,
    ) = withTimeout(10.seconds) {
        schoolsService.performCommand(
            IntegrityRequest(
                tokenString = integrityHelper.getIntegrityToken() ?: return@withTimeout,
                data = LoginEvent(
                    uuid = UUID.randomUUID().toString(),
                    schoolAddress = sdk.init(
                        unit.students.first().mapToStudentWithSemesters(
                            user = registerUser,
                            scrapperDomainSuffix = loginData.domainSuffix,
                            symbol = symbol,
                            unit = unit,
                            colors = emptyList(),
                        ).student
                    ).getSchool().address,
                    scraperBaseUrl = registerUser.scrapperBaseUrl.orEmpty(),
                    symbol = symbol.symbol,
                    schoolName = unit.schoolName,
                    schoolId = unit.schoolId,
                    loginType = registerUser.loginType?.name.orEmpty(),
                )
            )
        )
    }
}
