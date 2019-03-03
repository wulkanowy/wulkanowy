package io.github.wulkanowy.services.sync.works

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.data.repositories.note.NoteRepository
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.services.sync.channels.NewEntriesChannel
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.utils.getCompatColor
import io.reactivex.Completable
import javax.inject.Inject

class NoteWork @Inject constructor(
    private val context: Context,
    private val notificationManager: NotificationManagerCompat,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val noteRepository: NoteRepository,
    private val preferencesRepository: PreferencesRepository
) : Work {

    override fun create(): Completable {
        return studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getCurrentSemester(it).map { semester -> it to semester } }
            .flatMap { data ->
                noteRepository.getNotes(data.first, data.second, true, preferencesRepository.isNotificationsEnable)
                    .flatMap { noteRepository.getUnnotifiedNotes(data.first) }
            }.flatMapCompletable {
                if (it.isNotEmpty()) notify(it)
                noteRepository.updateNotes(it.onEach { note -> note.isNotified = true })
            }
    }

    private fun notify(notes: List<Note>) {
        notificationManager.notify(2, NotificationCompat.Builder(context, NewEntriesChannel.CHANNEL_ID)
            .setContentTitle(context.resources.getQuantityString(R.plurals.note_new_items, notes.size, notes.size))
            .setContentText(context.resources.getQuantityString(R.plurals.note_notify_new_items, notes.size, notes.size))
            .setSmallIcon(R.drawable.ic_stat_notify_note)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(context.getCompatColor(R.color.colorPrimary))
            .setContentIntent(
                PendingIntent.getActivity(context, 0,
                    MainActivity.getStartIntent(context).putExtra(MainActivity.EXTRA_START_MENU_INDEX, 4),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .setStyle(NotificationCompat.InboxStyle().run {
                setSummaryText(context.resources.getQuantityString(R.plurals.note_number_item, notes.size, notes.size))
                notes.forEach { addLine("${it.teacher}: ${it.category}") }
                this
            })
            .build())
    }
}

