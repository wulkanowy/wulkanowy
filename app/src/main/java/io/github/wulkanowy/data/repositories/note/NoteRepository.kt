package io.github.wulkanowy.data.repositories.note

import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val local: NoteLocal,
    private val remote: NoteRemote
) {

    suspend fun refreshNotes(student: Student, semester: Semester, notify: Boolean = false) {
        val new = remote.getNotes(student, semester)
        val old = local.getNotes(student).first()

        local.deleteNotes(old uniqueSubtract new)
        local.saveNotes((new uniqueSubtract old).onEach {
            if (it.date >= student.registrationDate.toLocalDate()) it.apply {
                isRead = false
                if (notify) isNotified = false
            }
        })
    }

    fun getNotes(student: Student, semester: Semester, notify: Boolean = false): Flow<List<Note>> {
        return local.getNotes(student).map {
            if (it.isNotEmpty()) return@map it
            refreshNotes(student, semester, notify)
            it
        }
    }

    fun getNotNotifiedNotes(student: Student): Flow<List<Note>> {
        return local.getNotes(student).map { it.filter { note -> !note.isNotified } }
    }

    suspend fun updateNote(note: Note) {
        return local.updateNotes(listOf(note))
    }

    suspend fun updateNotes(notes: List<Note>) {
        return local.updateNotes(notes)
    }
}
