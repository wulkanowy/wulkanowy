package io.github.wulkanowy.services.job

import android.annotation.SuppressLint
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.firebase.jobdispatcher.SimpleJobService
import dagger.android.AndroidInjection
import io.github.wulkanowy.data.repositories.AttendanceRepository
import io.github.wulkanowy.data.repositories.ExamRepository
import io.github.wulkanowy.data.repositories.GradeRepository
import io.github.wulkanowy.data.repositories.GradeSummaryRepository
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.SessionRepository
import io.github.wulkanowy.data.repositories.TimetableRepository
import io.github.wulkanowy.services.notification.GradeNotification
import io.github.wulkanowy.utils.friday
import io.github.wulkanowy.utils.isHolidays
import io.github.wulkanowy.utils.monday
import io.reactivex.Single
import org.threeten.bp.LocalDate
import timber.log.Timber
import javax.inject.Inject

class SyncWorker : SimpleJobService() {

    @Inject
    lateinit var session: SessionRepository

    @Inject
    lateinit var gradesDetails: GradeRepository

    @Inject
    lateinit var gradesSummary: GradeSummaryRepository

    @Inject
    lateinit var attendance: AttendanceRepository

    @Inject
    lateinit var exam: ExamRepository

    @Inject
    lateinit var timetable: TimetableRepository

    @Inject
    lateinit var prefRepository: PreferencesRepository

    companion object {
        const val WORK_TAG = "FULL_SYNC"
    }

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
    }

    @SuppressLint("CheckResult")
    override fun onRunJob(job: JobParameters?): Int {
        Timber.d("Synchronization started")

        val start = LocalDate.now().monday
        val end = LocalDate.now().friday

        if (start.isHolidays) return RESULT_FAIL_NORETRY

        var error: Throwable? = null

        return try {
            session.getSemesters(true)
                .map { it.single { semester -> semester.current } }
                .flatMapPublisher {
                    Single.merge(
                        listOf(
                            gradesDetails.getGrades(it, true, true),
                            gradesSummary.getGradesSummary(it, true),
                            attendance.getAttendance(it, start, end, true),
                            exam.getExams(it, start, end, true),
                            timetable.getTimetable(it, start, end, true)
                        )
                    )
                }
                .doFinally { if (prefRepository.notificationsEnable) sendNotifications() }
                .subscribe({}, { error = it })

            if (null !== error) {
                throw error!!
            }

            Timber.d("Synchronization successful")

            RESULT_SUCCESS
        } catch (e: Throwable) {
            Timber.e("Synchronization failed: ${e.localizedMessage}")
            JobService.RESULT_FAIL_RETRY
        }
    }

    @SuppressLint("CheckResult")
    private fun sendNotifications() {
        session.getSemesters(true)
            .map { it.single { semester -> semester.current } }
            .doFinally {
                Timber.d("All pending notifications sent")
            }
            .subscribe({ semester ->
                gradesDetails.getNewGrades(semester).subscribe { list ->
                    Timber.d("Found ${list.size} unread grades")
                    val gradeNotification = GradeNotification(applicationContext)
                    list.asSequence().filter { !it.notified }.map { grade ->
                        Timber.d("New grade id: ${grade.id}")
                        gradeNotification.sendNotification(
                            id = grade.id.toInt(),
                            title = grade.subject,
                            content = "${grade.gradeSymbol + (", " + grade.description).removeSuffix(", ")}: ${grade.entry}"
                        )
                        gradesDetails.updateGrade(grade.apply { notified = true }).subscribe()
                    }.toList()
                }
            }, { Timber.e("Notifications sending failed") })
    }
}
