package io.github.wulkanowy.data.repositories.homework

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.CalendarContract
import android.provider.CalendarContract.Instances
import android.provider.Settings.Global.getString
import androidx.core.content.ContextCompat
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.utils.friday
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.uniqueSubtract
import io.reactivex.Completable
import io.reactivex.Single
import org.threeten.bp.LocalDate
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton
import org.threeten.bp.LocalDate.now
import org.threeten.bp.ZoneOffset
import timber.log.Timber
import java.util.TimeZone

@Singleton
class HomeworkRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: HomeworkLocal,
    private val remote: HomeworkRemote,
    private val preferencesRepository: PreferencesRepository,
    private val context: Context
) {

    fun getHomework(semester: Semester, start: LocalDate, end: LocalDate, forceRefresh: Boolean = false): Single<List<Homework>> {
        return Single.fromCallable { start.monday to end.friday }.flatMap { (monday, friday) ->
            local.getHomework(semester, monday, friday).filter { !forceRefresh }
                .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                    .flatMap {
                        if (it) remote.getHomework(semester, monday, friday)
                        else Single.error(UnknownHostException())
                    }.flatMap { new ->
                        local.getHomework(semester, monday, friday).toSingle(emptyList())
                            .doOnSuccess { old ->
                                local.deleteHomework(old.uniqueSubtract(new))
                                local.saveHomework(new.uniqueSubtract(old))
                            }
                    }.flatMap { local.getHomework(semester, monday, friday).toSingle(emptyList()) })
        }
    }

    fun getNotCalendarSyncedHomework(semester: Semester): Single<List<Homework>> {
        return local.getHomework(semester, now().monday, now().friday).map { it.filter { homework -> !homework.calendarSync && preferencesRepository.isCalendarSyncEnable } }.toSingle(emptyList())
    }

    fun updateHomework(homework: List<Homework>): Completable {
        return Completable.fromCallable { local.updateHomework(homework) }
    }

    fun createCalendarEvents(homework: List<Homework>) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR)
            === PackageManager.PERMISSION_GRANTED) {
            homework.onEach {
                if (!isEventExists(
                        "${it.subject} - ${context.getString(R.string.homework_label)}",
                        now().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli().toString(),
                        now().atStartOfDay().plusDays(1).toInstant(ZoneOffset.UTC).toEpochMilli().toString()
                    )) {
                    val cr: ContentResolver = context.contentResolver
                    val event = ContentValues()
                    Timber.i(now().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli().toString())
                    event.apply {
                        put("calendar_id", preferencesRepository.calendarSyncId)
                        put("title", "${it.subject} - ${context.getString(R.string.homework_label)}")
                        put("description", "${it.content}\n\nWygenerowano przez Wulkanowy Dzienniczek")
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
