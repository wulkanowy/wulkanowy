package io.github.wulkanowy.utils;

import org.junit.Test;
import org.threeten.bp.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TimeUtilsTest {

    @Test
    public void getParsedDateTest() {
        assertEquals(LocalDate.of(1970, 1, 1),
                TimeUtils.getParsedDate("1970-01-01", "yyyy-MM-dd"));
    }

    @Test
    public void isDateInWeekInsideTest() {
        assertTrue(TimeUtils.isDateInWeek(
                LocalDate.of(2018, 5, 28),
                LocalDate.of(2018, 5, 31)
        ));
    }

    @Test
    public void isDateInWeekExtremesTest() {
        assertTrue(TimeUtils.isDateInWeek(
                LocalDate.of(2018, 5, 28),
                LocalDate.of(2018, 5, 28)
        ));

        assertTrue(TimeUtils.isDateInWeek(
                LocalDate.of(2018, 5, 28),
                LocalDate.of(2018, 6, 1)
        ));
    }

    @Test
    public void isDateInWeekOutOfTest() {
        assertFalse(TimeUtils.isDateInWeek(
                LocalDate.of(2018, 5, 28),
                LocalDate.of(2018, 6, 2)
        ));

        assertFalse(TimeUtils.isDateInWeek(
                LocalDate.of(2018, 5, 28),
                LocalDate.of(2018, 5, 27)
        ));
    }

    @Test
    public void isHolidaysInSchoolEndTest() {
        assertFalse(TimeUtils.isHolidays(LocalDate.of(2017, 6, 23), 2017));
        assertFalse(TimeUtils.isHolidays(LocalDate.of(2018, 6, 22), 2018));
        assertFalse(TimeUtils.isHolidays(LocalDate.of(2019, 6, 21), 2019));
        assertFalse(TimeUtils.isHolidays(LocalDate.of(2020, 6, 26), 2020));
        assertFalse(TimeUtils.isHolidays(LocalDate.of(2021, 6, 25), 2021));
        assertFalse(TimeUtils.isHolidays(LocalDate.of(2022, 6, 24), 2022));
        assertFalse(TimeUtils.isHolidays(LocalDate.of(2023, 6, 23), 2023));
        assertFalse(TimeUtils.isHolidays(LocalDate.of(2024, 6, 21), 2024));
        assertFalse(TimeUtils.isHolidays(LocalDate.of(2025, 6, 27), 2025));
    }

    @Test
    public void isHolidaysInHolidaysStartTest() {
        assertTrue(TimeUtils.isHolidays(LocalDate.of(2017, 6, 24), 2017));
        assertTrue(TimeUtils.isHolidays(LocalDate.of(2018, 6, 23), 2018));
        assertTrue(TimeUtils.isHolidays(LocalDate.of(2019, 6, 22), 2019));
        assertTrue(TimeUtils.isHolidays(LocalDate.of(2020, 6, 27), 2020));
        assertTrue(TimeUtils.isHolidays(LocalDate.of(2021, 6, 26), 2021));
        assertTrue(TimeUtils.isHolidays(LocalDate.of(2022, 6, 25), 2022));
        assertTrue(TimeUtils.isHolidays(LocalDate.of(2023, 6, 24), 2023));
        assertTrue(TimeUtils.isHolidays(LocalDate.of(2024, 6, 22), 2024));
        assertTrue(TimeUtils.isHolidays(LocalDate.of(2025, 6, 28), 2025));
    }

    @Test
    public void isHolidaysInHolidaysEndTest() {
        assertTrue(TimeUtils.isHolidays(LocalDate.of(2017, 9, 1), 2017)); // friday
        assertTrue(TimeUtils.isHolidays(LocalDate.of(2017, 9, 2), 2017)); // saturday
        assertTrue(TimeUtils.isHolidays(LocalDate.of(2017, 9, 3), 2017)); // sunday
        assertTrue(TimeUtils.isHolidays(LocalDate.of(2018, 9, 1), 2018)); // saturday
        assertTrue(TimeUtils.isHolidays(LocalDate.of(2018, 9, 2), 2018)); // sunday
        assertTrue(TimeUtils.isHolidays(LocalDate.of(2019, 9, 1), 2019)); // sunday
        assertTrue(TimeUtils.isHolidays(LocalDate.of(2020, 8, 31), 2020)); // monday
        assertTrue(TimeUtils.isHolidays(LocalDate.of(2021, 8, 31), 2021)); // tuesday
        assertTrue(TimeUtils.isHolidays(LocalDate.of(2022, 8, 31), 2022)); // wednesday
        assertTrue(TimeUtils.isHolidays(LocalDate.of(2023, 9, 1), 2023)); // friday
        assertTrue(TimeUtils.isHolidays(LocalDate.of(2023, 9, 2), 2023)); // saturday
        assertTrue(TimeUtils.isHolidays(LocalDate.of(2023, 9, 3), 2023)); // sunday
        assertTrue(TimeUtils.isHolidays(LocalDate.of(2024, 9, 1), 2024)); // sunday
        assertTrue(TimeUtils.isHolidays(LocalDate.of(2025, 8, 31), 2025)); // sunday
    }

    @Test
    public void isHolidaysInSchoolStartTest() {
        assertFalse(TimeUtils.isHolidays(LocalDate.of(2017, 9, 4), 2017)); // monday
        assertFalse(TimeUtils.isHolidays(LocalDate.of(2018, 9, 3), 2018)); // monday
        assertFalse(TimeUtils.isHolidays(LocalDate.of(2019, 9, 2), 2019)); // monday
        assertFalse(TimeUtils.isHolidays(LocalDate.of(2020, 9, 1), 2020)); // tuesday
        assertFalse(TimeUtils.isHolidays(LocalDate.of(2021, 9, 1), 2021)); // wednesday
        assertFalse(TimeUtils.isHolidays(LocalDate.of(2022, 9, 1), 2022)); // thursday
        assertFalse(TimeUtils.isHolidays(LocalDate.of(2023, 9, 4), 2023)); // monday
        assertFalse(TimeUtils.isHolidays(LocalDate.of(2024, 9, 2), 2024)); // monday
        assertFalse(TimeUtils.isHolidays(LocalDate.of(2025, 9, 1), 2025)); // monday
    }
}
