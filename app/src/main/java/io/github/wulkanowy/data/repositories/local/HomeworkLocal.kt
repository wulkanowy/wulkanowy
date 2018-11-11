package io.github.wulkanowy.data.repositories.local

import io.github.wulkanowy.data.db.dao.HomeworkDao
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Completable
import io.reactivex.Maybe
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeworkLocal @Inject constructor(private val homeworkDb: HomeworkDao) {

    fun getHomework(semester: Semester, date: LocalDate): Maybe<List<Homework>> {
        return homeworkDb.getHomework(semester.semesterId, semester.studentId, date).filter { !it.isEmpty() }
    }

    fun getNewHomework(semester: Semester): Maybe<List<Homework>> {
        return homeworkDb.getNewHomework(semester.semesterId, semester.studentId)
    }

    fun saveHomework(homework: List<Homework>) {
        homeworkDb.insertAll(homework)
    }

    fun updateHomework(homework: Homework): Completable {
        return Completable.fromCallable { homeworkDb.update(homework) }
    }

    fun updateHomeworkList(notes: List<Homework>): Completable {
        return Completable.fromCallable { homeworkDb.updateAll(notes) }
    }

    fun deleteHomework(homework: List<Homework>) {
        homeworkDb.deleteAll(homework)
    }
}
