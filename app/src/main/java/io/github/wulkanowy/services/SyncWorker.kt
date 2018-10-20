package io.github.wulkanowy.services

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.AttendanceRepository
import io.github.wulkanowy.data.repositories.ExamRepository
import io.github.wulkanowy.data.repositories.GradeRepository
import io.github.wulkanowy.data.repositories.GradeSummaryRepository
import io.github.wulkanowy.data.repositories.SessionRepository
import io.github.wulkanowy.data.repositories.TimetableRepository
import io.github.wulkanowy.di.Provider
import io.github.wulkanowy.utils.friday
import io.github.wulkanowy.utils.monday
import io.reactivex.Flowable
import io.reactivex.Single
import org.threeten.bp.LocalDate
import timber.log.Timber
import java.io.Serializable
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

    init {
        Provider.appComponent.inject(this)
    }

    @SuppressLint("CheckResult")
    override fun doWork(): Result {
        Timber.d("Synchronization started")

        val monday = LocalDate.now().monday
        val friday = LocalDate.now().friday

        return try {
            session.getSemesters()
                .map { it.single { semester -> semester.current } }
                .map {
                    Single.merge(
                        gradesDetails.getGrades(it, true),
                        gradesSummary.getGradesSummary(it, true)
                    ).subscribe()

                    syncByWeek(it, monday, friday).subscribe()
                    syncByWeek(it, monday.plusDays(7), friday.plusDays(7)).subscribe()
                }.blockingGet()

            Timber.d("Synchronization successful")

            Result.RETRY
        } catch (e: Exception) {
            Timber.d("Synchronization failed: ${e.localizedMessage}")
            Result.RETRY
        }
    }

    private fun syncByWeek(semester: Semester, start: LocalDate, end: LocalDate): Flowable<List<Serializable>> {
        return Single.merge(
            attendance.getAttendance(semester, start, end, true),
            exam.getExams(semester, start, end, true),
            timetable.getTimetable(semester, start, end, true)
        )
    }
}
