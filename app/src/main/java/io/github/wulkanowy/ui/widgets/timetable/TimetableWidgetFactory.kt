package io.github.wulkanowy.ui.widgets.timetable

import android.content.Context
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView.INVALID_POSITION
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.SharedPrefHelper
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.data.repositories.SessionRepository
import io.github.wulkanowy.data.repositories.TimetableRepository
import io.github.wulkanowy.utils.toFormattedString
import io.reactivex.disposables.CompositeDisposable
import org.threeten.bp.LocalDate
import timber.log.Timber
import javax.inject.Inject

class TimetableWidgetFactory @Inject constructor(
    private val timetableRepository: TimetableRepository,
    private val sessionRepository: SessionRepository,
    private val sharedPref: SharedPrefHelper,
    private val context: Context
) : RemoteViewsService.RemoteViewsFactory {

    private lateinit var currentDate: LocalDate

    private val disposable: CompositeDisposable = CompositeDisposable()

    private var lessons = emptyList<Timetable>()

    override fun getLoadingView() = null

    override fun hasStableIds() = true

    override fun getCount() = lessons.size

    override fun getViewTypeCount() = 1

    override fun getItemId(position: Int) = position.toLong()

    override fun onCreate() {}

    override fun onDataSetChanged() {
        currentDate = LocalDate.ofEpochDay(sharedPref.getLong("timetable_widget", -1))
        if (sessionRepository.isSessionSaved) {
            disposable.add(sessionRepository.getSemesters()
                .map { it.single { item -> item.current } }
                .flatMap { timetableRepository.getTimetable(it, currentDate, currentDate) }
                .map { item -> item.sortedBy { it.number } }
                .subscribe({ lessons = it })
                { Timber.e(it, "An error has occurred while downloading data for the widget") })
        }
    }

    override fun getViewAt(position: Int): RemoteViews? {
        if (position == INVALID_POSITION) return null

        return RemoteViews(context.packageName, R.layout.item_widget_timetable).apply {
            lessons[position].let {
                setTextViewText(R.id.timetableWidgetItemSubject, it.subject)
                setTextViewText(R.id.timetableWidgetItemTime, it.start.toFormattedString("HH:mm") +
                    " - ${it.end.toFormattedString("HH:mm")}")

                if (it.room.isNotBlank()) {
                    setTextViewText(R.id.timetableWidgetItemRoom, "${context.getString(R.string.timetable_room)} ${it.room}")
                } else setTextViewText(R.id.timetableWidgetItemRoom, "")

                if (it.info.isNotBlank()) {
                    setViewVisibility(R.id.timetableWidgetItemDescription, VISIBLE)
                    setTextViewText(R.id.timetableWidgetItemDescription, it.info.capitalize())
                } else setViewVisibility(R.id.timetableWidgetItemDescription, GONE)

                if (it.changes) {
                    setInt(R.id.timetableWidgetItemSubject, "setPaintFlags",
                        STRIKE_THRU_TEXT_FLAG or ANTI_ALIAS_FLAG)
                } else {
                    setInt(R.id.timetableWidgetItemSubject, "setPaintFlags", ANTI_ALIAS_FLAG)
                }
            }
        }
    }

    override fun onDestroy() {
        disposable.clear()
    }
}
