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
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
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

    private val isEduOneChecked = AtomicBoolean(false)

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
        if (student.isEduOne) return true
        if (studentDb.loadById(student.id)?.isEduOne == true) return true

        val currentSemester = semesterDb.loadAll(
            studentId = student.studentId,
            classId = student.classId,
        ).getCurrentOrLast()
        val initializedSdk = buildSdk(student, currentSemester, false)
        val newCurrentStudent = initializedSdk.getCurrentStudent()
            ?: throw IllegalStateException("Can't get current student from WulkanowySDK")

        if (!newCurrentStudent.isEduOne) return false

        val studentIsEduOne = StudentIsEduOne(true)
        studentDb.update(studentIsEduOne)

        return true
    }
}
