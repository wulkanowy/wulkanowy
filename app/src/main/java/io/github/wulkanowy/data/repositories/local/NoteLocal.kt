package io.github.wulkanowy.data.repositories.local

import io.github.wulkanowy.data.db.dao.NoteDao
import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Maybe
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteLocal @Inject constructor(private val noteDb: NoteDao) {

    fun getNotes(semester: Semester): Maybe<List<Note>> {
        return noteDb.getNotes(semester.semesterId, semester.studentId).filter { !it.isEmpty() }
    }

    fun saveNotes(notes: List<Note>) {
        noteDb.insertAll(notes)
    }

    fun deleteNotes(notes: List<Note>) {
        noteDb.deleteAll(notes)
    }
}
