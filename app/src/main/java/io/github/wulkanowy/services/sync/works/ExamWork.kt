package io.github.wulkanowy.services.sync.works

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.CalendarContract
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.exam.ExamRepository
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.utils.friday
import io.github.wulkanowy.utils.monday
import io.reactivex.Completable
import org.threeten.bp.LocalDate.now
import org.threeten.bp.ZoneOffset
import timber.log.Timber
import java.util.TimeZone
import javax.inject.Inject

class ExamWork @Inject constructor(
    private val examRepository: ExamRepository,
    private val context: Context,
    private val preferencesRepository: PreferencesRepository): Work {

    override fun create(student: Student, semester: Semester): Completable {
        return examRepository.getExams(semester, now().monday, now().friday, true)
            .flatMap { examRepository.getNotCalendarSyncedExams(semester) }
            .flatMapCompletable {
                if(it.isNotEmpty()) calendarSync(it)
                examRepository.updateExams(it.onEach { exam -> exam.calendarSync = true })
            }
    }

    @SuppressLint("MissingPermission")
    private fun calendarSync( exams: List<Exam> ){
        exams.onEach {
            val cr: ContentResolver = context.contentResolver
            val event = ContentValues()
            Timber.i(now().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli().toString())
            event.apply {
                put("calendar_id", preferencesRepository.calendarSyncId);
                put("title", "${it.subject} - ${it.type}")
                put("description", "${it.description}\n\nWygenerowano przez Wulkanowy Dzienniczek")
                put("allDay", 1)
                put("dtstart", it.date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli())
                put("duration", "PT1D");
                put("eventTimezone", TimeZone.getTimeZone("GMT").id)
                put("hasAlarm", 0);
            }
            val uri: Uri? = cr.insert(CalendarContract.Events.CONTENT_URI, event)
        }
    }
}
