package io.github.wulkanowy.services.job

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import io.github.wulkanowy.data.repositories.AttendanceRepository
import io.github.wulkanowy.data.repositories.ExamRepository
import io.github.wulkanowy.data.repositories.GradeRepository
import io.github.wulkanowy.data.repositories.GradeSummaryRepository
import io.github.wulkanowy.data.repositories.SessionRepository
import io.github.wulkanowy.data.repositories.TimetableRepository
import io.github.wulkanowy.di.Provider
import io.github.wulkanowy.services.notification.GradeNotification
import io.github.wulkanowy.utils.friday
import io.github.wulkanowy.utils.isHolidays
import io.github.wulkanowy.utils.monday
import io.reactivex.Single
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import timber.log.Timber
import java.sql.Date
import java.util.Random
import javax.inject.Inject

class SyncWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {

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

    companion object {
        const val WORK_TAG = "FULL_SYNC"
    }

    init {
        Provider.appComponent.inject(this)
    }

    @SuppressLint("CheckResult")
    override fun doWork(): Result {
        Timber.d("Synchronization started")

        val start = LocalDate.now().monday
        val end = LocalDate.now().friday

        if (start.isHolidays) return Result.FAILURE

        var error: Throwable? = null

        return try {
            session.getSemesters(true)
                .map { it.single { semester -> semester.current } }
                .flatMapPublisher {
                    Single.merge(
                        listOf(
                            gradesDetails.getGrades(it, true),
                            gradesSummary.getGradesSummary(it, true),
                            attendance.getAttendance(it, start, end, true),
                            exam.getExams(it, start, end, true),
                            timetable.getTimetable(it, start, end, true)
                        )
                    )
                }
                .doFinally { sendNotifications() }
                .subscribe({}, { error = it })

            if (null !== error) {
                throw error!!
            }

            Timber.d("Synchronization successful")

            Result.SUCCESS
        } catch (e: Throwable) {
            Timber.d("Synchronization failed: ${e.localizedMessage}")
            Result.RETRY
        }
    }

    @SuppressLint("CheckResult")
    private fun sendNotifications() {
        val notify = GradeNotification(applicationContext)

        notify.sendNotification(Random().nextInt(1000), "Coś działa", "Powiadomienie wysłano o ${LocalDateTime.now()}")

        Timber.d("Search for notification to send")
        gradesDetails.getNewGrades().subscribe {
            it.map { grade ->
                Timber.d("New grade id: ${grade.id}")
                notify.sendNotification(
                    grade.id.toInt(),
                    grade.subject,
                    "${grade.gradeSymbol + (", " + grade.description).removeSuffix(", ")}: ${grade.entry}"
                )
            }
        }
        Timber.d("All pending notifications sent")
    }
}
