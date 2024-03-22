package io.github.wulkanowy.data

import com.chuckerteam.chucker.api.ChuckerInterceptor
import io.github.wulkanowy.data.db.dao.SemesterDao
import io.github.wulkanowy.data.db.dao.StudentDao
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.StudentIsEduOne
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.RemoteConfigHelper
import io.github.wulkanowy.utils.WebkitCookieManagerProxy
import io.github.wulkanowy.utils.getCurrentOrLast
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WulkanowySdkFactory @Inject constructor(
    private val chuckerInterceptor: ChuckerInterceptor,
    private val remoteConfig: RemoteConfigHelper,
    private val webkitCookieManagerProxy: WebkitCookieManagerProxy,
    private val semesterDb: SemesterDao,
    private val studentDb: StudentDao,
) {

    private val eduOneMutex = Mutex()

    private val sdk = Sdk().apply {
        androidVersion = android.os.Build.VERSION.RELEASE
        buildTag = android.os.Build.MODEL
        userAgentTemplate = remoteConfig.userAgentTemplate
        setSimpleHttpLogger { Timber.d(it) }
        setAdditionalCookieManager(webkitCookieManagerProxy)

        // for debug only
        addInterceptor(chuckerInterceptor, network = true)
    }

    fun create() = sdk

    suspend fun create(student: Student, semester: Semester? = null): Sdk {
        val overrideIsEduOne = migrateStudentToEduOneIfNecessary(student)
        return buildSdk(student, semester, overrideIsEduOne)
    }

    private fun buildSdk(student: Student, semester: Semester?, isStudentEduOne: Boolean): Sdk {
        return create().apply {
            email = student.email
            password = student.password
            symbol = student.symbol
            schoolSymbol = student.schoolSymbol
            studentId = student.studentId
            classId = student.classId
            emptyCookieJarInterceptor = true
            isEduOne = isStudentEduOne

            if (Sdk.Mode.valueOf(student.loginMode) == Sdk.Mode.HEBE) {
                mobileBaseUrl = student.mobileBaseUrl
            } else {
                scrapperBaseUrl = student.scrapperBaseUrl
                domainSuffix = student.scrapperDomainSuffix
                loginType = Sdk.ScrapperLoginType.valueOf(student.loginType)
            }

            mode = Sdk.Mode.valueOf(student.loginMode)
            mobileBaseUrl = student.mobileBaseUrl
            keyId = student.certificateKey
            privatePem = student.privateKey

            if (semester != null) {
                diaryId = semester.diaryId
                kindergartenDiaryId = semester.kindergartenDiaryId
                schoolYear = semester.schoolYear
                unitId = semester.unitId
            }
        }
    }

    private suspend fun migrateStudentToEduOneIfNecessary(student: Student): Boolean {
        if (student.isEduOne != null) return student.isEduOne

        eduOneMutex.withLock {
            val studentFromDatabase = studentDb.loadById(student.id)
            if (studentFromDatabase?.isEduOne != null) {
                return studentFromDatabase.isEduOne
            }

            val currentSemester = semesterDb.loadAll(
                studentId = student.studentId,
                classId = student.classId,
            ).getCurrentOrLast()
            val initializedSdk = buildSdk(student, currentSemester, false)
            val newCurrentStudent = runCatching { initializedSdk.getCurrentStudent() }
                .onFailure { Timber.e(it, "Can't get current student from WulkanowySDK") }
                .getOrNull() ?: return false

            val studentIsEduOne = StudentIsEduOne(
                id = student.id,
                isEduOne = newCurrentStudent.isEduOne
            )
            studentDb.update(studentIsEduOne)
            return newCurrentStudent.isEduOne
        }
    }
}
