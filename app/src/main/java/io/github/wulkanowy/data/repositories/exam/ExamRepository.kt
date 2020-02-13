package io.github.wulkanowy.data.repositories.exam

import android.Manifest
import android.R.attr.end
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import android.provider.CalendarContract.Instances
import androidx.core.content.ContextCompat
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.utils.friday
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.uniqueSubtract
import io.reactivex.Completable
import io.reactivex.Single
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDate.now
import org.threeten.bp.ZoneOffset
import timber.log.Timber
import java.net.UnknownHostException
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExamRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: ExamLocal,
    private val remote: ExamRemote,
    private val preferencesRepository: PreferencesRepository,
    private val context: Context
) {

    fun getExams(semester: Semester, startDate: LocalDate, endDate: LocalDate, forceRefresh: Boolean = false): Single<List<Exam>> {
        return Single.fromCallable { startDate.monday to endDate.friday }
            .flatMap { dates ->
                local.getExams(semester, dates.first, dates.second).filter { !forceRefresh }
                    .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                        .flatMap {
                            if (it) remote.getExams(semester, dates.first, dates.second)
                            else Single.error(UnknownHostException())
                        }.flatMap { new ->
                            local.getExams(semester, dates.first, dates.second)
                                .toSingle(emptyList())
                                .doOnSuccess { old ->
                                    local.deleteExams(old.uniqueSubtract(new))
                                    local.saveExams(new.uniqueSubtract(old))
                                }
                        }.flatMap {
                            local.getExams(semester, dates.first, dates.second)
                                .toSingle(emptyList())
                        }).map { list -> list.filter { it.date in startDate..endDate } }
            }
    }

    fun getNotCalendarSyncedExams(semester: Semester): Single<List<Exam>> {

        return local.getExams(semester, now().monday, now().plusWeeks(4).friday).map { it.filter { exam -> !exam.calendarSync && preferencesRepository.isCalendarSyncEnable } }.toSingle(emptyList())
    }


    fun updateExams(exams: List<Exam>): Completable {
        return Completable.fromCallable { local.updateExams(exams) }
    }

    fun createCalendarEvents(exams: List<Exam>) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR)
            === PackageManager.PERMISSION_GRANTED) {
            exams.onEach {
                if (!isEventExists(
                        "${it.subject} - ${it.type}",
                        now().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli().toString(),
                        now().atStartOfDay().plusDays(1).toInstant(ZoneOffset.UTC).toEpochMilli().toString()
                    )) {
                    val cr: ContentResolver = context.contentResolver
                    val event = ContentValues()
                    Timber.i(now().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli().toString())
                    event.apply {
                        put("calendar_id", preferencesRepository.calendarSyncId)
                        put("title", "${it.subject} - ${it.type}")
                        put("description", "${it.description}\n\nWygenerowano przez Wulkanowy Dzienniczek")
                        put("allDay", 1)
                        put("dtstart", it.date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli())
                        put("duration", "PT1D")
                        put("eventTimezone", TimeZone.getTimeZone("GMT").id)
                        put("hasAlarm", 0)
                    }
                    cr.insert(CalendarContract.Events.CONTENT_URI, event)
                }
            }
        }
    }

    fun isEventExists(name: String, dtstart: String, dtend: String): Boolean{
        val fields = arrayOf(
            Instances._ID,
            Instances.BEGIN,
            Instances.END,
            Instances.EVENT_ID)
        val cursor: Cursor = Instances.query(context.contentResolver, fields, dtstart.toLong(), dtend.toLong(), name)
        return cursor.count > 0
    }
}
