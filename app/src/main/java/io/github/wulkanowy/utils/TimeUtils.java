package io.github.wulkanowy.utils;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.Year;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.TemporalAdjusters;

import java.util.ArrayList;
import java.util.List;

public final class TimeUtils {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(AppConstant.DATE_PATTERN);

    private TimeUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static LocalDate getParsedDate(String dateString, String dateFormat) {
        return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(dateFormat));
    }

    public static List<String> getMondaysFromCurrentSchoolYear() {
        LocalDate startDate = LocalDate.of(getCurrentSchoolYear(), 9, 1);
        LocalDate endDate = LocalDate.of(getCurrentSchoolYear() + 1, 8, 31);

        List<String> dateList = new ArrayList<>();

        LocalDate thisMonday = startDate.with(DayOfWeek.MONDAY);

        if (startDate.isAfter(thisMonday)) {
            startDate = thisMonday.plusWeeks(1);
        } else {
            startDate = thisMonday;
        }

        while (startDate.isBefore(endDate)) {
            dateList.add(startDate.format(formatter));
            startDate = startDate.plusWeeks(1);
        }
        return dateList;
    }

    public static int getCurrentSchoolYear() {
        LocalDate localDate = LocalDate.now();
        return localDate.getMonthValue() <= 8 ? localDate.getYear() - 1 : localDate.getYear();
    }

    public static String getDateOfCurrentMonday(boolean normalize) {
        LocalDate currentDate = LocalDate.now();

        if (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY && normalize) {
            currentDate = currentDate.plusDays(2);
        } else if (currentDate.getDayOfWeek() == DayOfWeek.SUNDAY && normalize) {
            currentDate = currentDate.plusDays(1);
        } else {
            currentDate = currentDate.with(DayOfWeek.MONDAY);
        }
        return currentDate.format(formatter);
    }

    public static int getTodayOrNextDayValue(boolean nextDay) {
        DayOfWeek day = LocalDate.now().getDayOfWeek();
        if (nextDay) {
            if (day == DayOfWeek.SUNDAY) {
                return 0;
            }
            return day.getValue();
        }
        return day.getValue() - 1;
    }

    public static String getTodayOrNextDay(boolean nextDay) {
        LocalDate current = LocalDate.now();
        return nextDay ? current.plusDays(1).format(formatter) : current.format(formatter);
    }

    public static boolean isDateInWeek(LocalDate firstWeekDay, LocalDate date) {
        return date.isAfter(firstWeekDay.minusDays(1)) && date.isBefore(firstWeekDay.plusDays(5));
    }

    public static boolean isHolidays() {
        return isHolidays(LocalDate.now(), Year.now().getValue());
    }

    /**
     * <a href="http://prawo.sejm.gov.pl/isap.nsf/DocDetails.xsp?id=WDU20160001335">Dz.U. 2016 poz. 1335</a>
     */
    public static boolean isHolidays(LocalDate day, int year) {
        LocalDate lastSchoolDay = LocalDate
                .of(year, 6, 20)
                .with(TemporalAdjusters.next(DayOfWeek.FRIDAY));

        LocalDate firstSchoolDay = LocalDate.of(year, 9, 1);
        switch (firstSchoolDay.getDayOfWeek()) {
            case FRIDAY:
            case SATURDAY:
            case SUNDAY:
                firstSchoolDay = firstSchoolDay.with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
                break;
        }

        return day.isAfter(lastSchoolDay) && day.isBefore(firstSchoolDay);
    }
}
