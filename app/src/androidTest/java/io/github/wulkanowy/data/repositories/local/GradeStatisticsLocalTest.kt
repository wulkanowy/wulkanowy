package io.github.wulkanowy.data.repositories.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.entities.GradeStatistics
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.gradestatistics.GradeStatisticsLocal
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class GradeStatisticsLocalTest {

    private lateinit var gradeStatisticsLocal: GradeStatisticsLocal

    private lateinit var testDb: AppDatabase

    @Before
    fun createDb() {
        testDb = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .build()
        gradeStatisticsLocal = GradeStatisticsLocal(testDb.gradeStatistics)
    }

    @After
    fun closeDb() {
        testDb.close()
    }

    @Test
    fun saveAndReadTest() {
        gradeStatisticsLocal.saveGradesStatistics(listOf(
            getGradeStatistics("Matematyka", 1),
            getGradeStatistics("Fizyka", 2),
            getGradeStatistics("Chemia", 2)
        ))

        val stats = gradeStatisticsLocal.getGradesStatistics(
            Semester(1, 2, "", 2, 2, true),
            "Matematyka"
        ).blockingGet()
        assertEquals(2, stats.size)
        assertEquals(stats[0].subject, "Fizyka")
        assertEquals(stats[1].subject, "Chemia")
    }

    private fun getGradeStatistics(subject: String, number: Int): GradeStatistics {
        return GradeStatistics(
            1, number, subject, 5, 5
        )
    }
}
