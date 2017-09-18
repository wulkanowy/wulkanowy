package io.github.wulkanowy.api.attendance;

import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.StudentAndParent;

public class Statistics {

    private StudentAndParent snp;

    private String attendancePageUrl = "Frekwencja.mvc?data={tick}&idPrzedmiot={subject}";

    public Statistics(StudentAndParent snp) {
        this.snp = snp;
    }

    public Types getTypesTable() throws IOException {
        return getTypesTable("");
    }

    public Types getTypesTable(String tick) throws IOException {
        return getTypesTable(tick, -1);
    }

    public Types getTypesTable(String tick, Integer subjectId) throws IOException {
        Element mainContainer = snp.getSnPPageDocument(attendancePageUrl
                .replace("{tick}", tick)
                .replace("{subject}", subjectId.toString())
        ).select(".mainContainer").first();

        Element table = mainContainer.select("table:nth-of-type(2)").first();

        Elements headerCells = table.select("thead th");
        List<Type> typeList = new ArrayList<>();

        Elements typesRows = table.select("tbody tr");

        // fill types with months
        for (Element row : typesRows) {
            Elements monthsCells = row.select("td");

            List<Month> monthList = new ArrayList<>();

            // iterate over month in type, first column is empty, last is `total`; <0, n-1>
            for (int i = 1; i < monthsCells.size() - 1; i++) {
                monthList.add(new Month()
                        .setValue(NumberUtils.toInt(monthsCells.get(i).text(), 0))
                        .setName(headerCells.get(i).text()));
            }

            typeList.add(new Type()
                    .setTotal(NumberUtils.toInt(monthsCells.last().text(), 0))
                    .setName(monthsCells.get(0).text())
                    .setMonthList(monthList));
        }

        String total = mainContainer.select("h2").text().split(": ")[1];

        return new Types()
                .setTotal(NumberUtils.toDouble(total.replace("%", "").replace(",", ".")))
                .setTypeList(typeList);
    }
}
