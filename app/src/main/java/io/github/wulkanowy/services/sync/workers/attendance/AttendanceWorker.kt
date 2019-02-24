package io.github.wulkanowy.services.sync.workers.attendance

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.wulkanowy.data.repositories.attendance.AttendanceRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.services.sync.factory.InjectableWorkerFactory
import io.github.wulkanowy.utils.friday
import io.github.wulkanowy.utils.monday
import io.reactivex.Single
import org.threeten.bp.LocalDate

class AttendanceWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val attendanceRepository: AttendanceRepository
) : RxWorker(appContext, workerParameters) {

    override fun createWork(): Single<Result> {
        return studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getCurrentSemester(it) }
            .flatMap { attendanceRepository.getAttendance(it, LocalDate.now().monday, LocalDate.now().friday, true) }
            .map { Result.success() }
            .onErrorReturn { Result.failure() }
    }

    @AssistedInject.Factory
    interface Factory : InjectableWorkerFactory
}

