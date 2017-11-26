package io.github.wulkanowy.api.notes;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.StudentAndParent;

public class AchievementsList {

    private StudentAndParent snp = null;

    private List<String> achievements = new ArrayList<>();

    private static final String notesPageUrl = "UwagiOsiagniecia.mvc/Wszystkie";

    public AchievementsList(StudentAndParent snp) {
        this.snp = snp;
    }

    public List<String> getAllAchievements() throws IOException {
        Element pageFragment = snp.getSnPPageDocument(notesPageUrl)
                .select(".mainContainer > div").get(1);
        Elements items = pageFragment.select("article");

        for (Element item : items) {
            achievements.add(item.text());
        }

        return achievements;
    }
}
