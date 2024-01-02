package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.NoteDao
import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.networkBoundResource
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.ui.modules.dashboard.DashboardItem
import io.github.wulkanowy.utils.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val noteDb: NoteDao,
    private val sdk: Sdk,
    private val refreshHelper: AutoRefreshHelper,
    private val preferencesRepository: PreferencesRepository
) {
    private val saveFetchResultMutex = Mutex()

    private val cacheKey = "note"

    fun getNotes(
        student: Student,
        semester: Semester,
        forceRefresh: Boolean,
        notify: Boolean = false,
    ) = networkBoundResource(
        mutex = saveFetchResultMutex,
        isResultEmpty = { it.isEmpty() },
        shouldFetch = {
            val isExpired = refreshHelper.shouldBeRefreshed(
                getRefreshKey(cacheKey, semester)
            )
            it.isEmpty() || forceRefresh || isExpired
        },
        query = {
            val notesHidden = preferencesRepository
                .selectedHiddenSettingTiles
                .contains(DashboardItem.HiddenSettingTile.NOTES)

            if (notesHidden) {
                noteDb.pretendLoadingAll(student.studentId)
            } else {
                noteDb.loadAll(student.studentId)
            }
        },
        fetch = { listOf<Note>() },
        saveFetchResult = { old, new ->
            noteDb.deleteAll(old uniqueSubtract new)
            noteDb.insertAll((new uniqueSubtract old).onEach {
                if (it.date >= student.registrationDate.toLocalDate()) it.apply {
                    isRead = false
                    if (notify) isNotified = false
                }
            })

            refreshHelper.updateLastRefreshTimestamp(getRefreshKey(cacheKey, semester))
        }
    )

    fun getNotesFromDatabase(student: Student): Flow<List<Note>> {
        val notesHidden = preferencesRepository
            .selectedHiddenSettingTiles
            .contains(DashboardItem.HiddenSettingTile.NOTES)

        return if (notesHidden) {
            noteDb.pretendLoadingAll(student.studentId)
        } else {
            noteDb.loadAll(student.studentId)
        }
    }

    suspend fun updateNote(note: Note) {
        noteDb.updateAll(listOf(note))
    }

    suspend fun updateNotes(notes: List<Note>) {
        noteDb.updateAll(notes)
    }
}
