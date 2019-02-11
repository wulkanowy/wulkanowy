package io.github.wulkanowy.data.repositories.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.entities.Realized
import io.github.wulkanowy.data.db.entities.Semester
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class RealizedLocalTest {

    private lateinit var realizedLocal: RealizedLocal

    private lateinit var testDb: AppDatabase

    @Before
    fun createDb() {
        testDb = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .build()
        realizedLocal = RealizedLocal(testDb.realizedDao)
    }

    @After
    fun closeDb() {
        testDb.close()
    }

    @Test
    fun saveAndReadTest() {
        realizedLocal.saveRealized(listOf(
            getRealized(LocalDate.of(2018, 9, 10), 1),
            getRealized(LocalDate.of(2018, 9, 14), 2),
            getRealized(LocalDate.of(2018, 9, 17), 3)
        ))

        val realized = realizedLocal
            .getRealized(Semester(1, 1, 2, "", 3, 1),
                LocalDate.of(2018, 9, 10),
                LocalDate.of(2018, 9, 14)
            )
            .blockingGet()
        assertEquals(2, realized.size)
        assertEquals(realized[0].date, LocalDate.of(2018, 9, 10))
        assertEquals(realized[1].date, LocalDate.of(2018, 9, 14))
    }

    private fun getRealized(date: LocalDate, number: Int): Realized {
        return Realized(1, 2, date, number, "", "", "", "", "", "", "")
    }
}
