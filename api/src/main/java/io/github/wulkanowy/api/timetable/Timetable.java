package io.github.wulkanowy.api.timetable;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.StudentAndParent;

public class Timetable {

    private StudentAndParent snp;

    private static final String TIMETABLE_PAGE_URL = "Lekcja.mvc/PlanLekcji?data=";

    public Timetable(StudentAndParent snp) {
        this.snp = snp;
    }

    public Week getWeekTable() throws IOException {
        return getWeekTable("");
    }

    public Week getWeekTable(final String tick) throws IOException {
        Element table = snp.getSnPPageDocument(TIMETABLE_PAGE_URL + tick)
                .select(".mainContainer .presentData").first();

        Elements tableHeaderCells = table.select("thead th");
        List<Day> days = new ArrayList<>();

        for (int i = 2; i < 7; i++) {
            String[] dayHeaderCell = tableHeaderCells.get(i).html().split("<br>");
            boolean isFreeDay = tableHeaderCells.get(i).hasClass("free-day");

            Day day = new Day();
            day.setDayName(dayHeaderCell[0]);
            day.setDate(dayHeaderCell[1].trim());

            if (isFreeDay) {
                day.setFreeDay(true);
                day.setFreeDayName(dayHeaderCell[2]);
            }

            days.add(day);
        }

        Elements hoursInDays = table.select("tbody tr");

        // fill days in week with lessons
        for (Element row : hoursInDays) {
            Elements hours = row.select("td");

            // fill hours in day
            for (int i = 2; i < hours.size(); i++) {
                Lesson lesson = new Lesson();

                Elements e = hours.get(i).select("div");
                switch (e.size()) {
                    case 1:
                        lesson = getLessonFromElement(e.first());
                        break;
                    case 3:
                        lesson = getLessonFromElement(e.get(1));
                        break;
                    default:
                        lesson.setEmpty(true);
                        break;
                }

                String[] startEndEnd = hours.get(1).text().split(" ");
                lesson.setStartTime(startEndEnd[0]);
                lesson.setEndTime(startEndEnd[1]);
                lesson.setDate(days.get(i - 2).getDate());
                lesson.setNumber(hours.get(0).text());

                days.get(i - 2).setLesson(lesson);
            }
        }

        Element startDayCellHeader = tableHeaderCells.get(2);
        String[] dayDescription = startDayCellHeader.html().split("<br>");

        return new Week()
                .setStartDayDate(dayDescription[1])
                .setDays(days);
    }

    private Lesson getLessonFromElement(Element e) {
        Lesson lesson = new Lesson();
        Elements spans = e.select("span");

        adTypeInfo(lesson, spans);
        addNormalLessonInfo(lesson, spans);
        addGroupLessonInfo(lesson, spans);

        return lesson;
    }

    private void adTypeInfo(Lesson lesson, Elements spans) {
        if (spans.first().hasClass(Lesson.CLASS_PLANNING)) {
            lesson.setPlanning(true);
        }

        if (spans.first().hasClass(Lesson.CLASS_MOVED_OR_CANCELED)) {
            lesson.setMovedOrCanceled(true);
        }

        if (spans.first().hasClass(Lesson.CLASS_NEW_MOVED_IN_OR_CHANGED)) {
            lesson.setNewMovedInOrChanged(true);
        }

        if (spans.last().hasClass(Lesson.CLASS_REALIZED) || "".equals(spans.first().attr("class"))) {
            lesson.setRealized(true);
        }
    }

    private void addNormalLessonInfo(Lesson lesson, Elements spans) {
        if (3 == spans.size()) {
            lesson.setSubject(spans.get(0).text());
            lesson.setTeacher(spans.get(1).text());
            lesson.setRoom(spans.get(2).text());
        }

        if (4 <= spans.size() && spans.last().hasClass(Lesson.CLASS_REALIZED)) {
            lesson.setSubject(spans.first().text());
            lesson.setTeacher(spans.get(1).text());
            lesson.setRoom(spans.get(2).text());
            lesson.setDescription(StringUtils.substringBetween(spans.last().text(), "(", ")"));
        }
    }

    private void addGroupLessonInfo(Lesson lesson, Elements spans) {
        if (4 <= spans.size() && !spans.last().hasClass(Lesson.CLASS_REALIZED)) {
            lesson.setDivisionIntoGroups(true);
            lesson.setSubject(spans.first().text());
            lesson.setTeacher(spans.get(2).text());
            lesson.setRoom(spans.last().text());
        }

        if (5 == spans.size()) {
            lesson.setDivisionIntoGroups(true);
            lesson.setSubject(spans.first().text());
            lesson.setTeacher(spans.get(2).text());
            lesson.setRoom(spans.get(3).text());
        }

        if ((4 == spans.size() && !spans.last().hasClass(Lesson.CLASS_REALIZED) || 5 == spans.size())) {
            String[] subjectNameArray = lesson.getSubject().split(" ");
            String groupName = subjectNameArray[subjectNameArray.length - 1];
            lesson.setSubject(lesson.getSubject().replace(" " + groupName, ""));
            lesson.setGroupName(StringUtils.substringBetween(groupName, "[", "]"));
        }
    }
}
