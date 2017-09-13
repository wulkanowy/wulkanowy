package io.github.wulkanowy.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

@Entity(nameInDb = "Subject")
public class Subject {

    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "NAME")
    private String name;

    @Property(nameInDb = "PREDICTED_RATING")
    private String predictedRating;

    @Property(nameInDb = "FINAL_RATING")
    private String finalRating;

    @Generated(hash = 219279018)
    public Subject(Long id, String name, String predictedRating,
                   String finalRating) {
        this.id = id;
        this.name = name;
        this.predictedRating = predictedRating;
        this.finalRating = finalRating;
    }

    @Generated(hash = 1617906264)
    public Subject() {
    }

    public Long getId() {
        return id;
    }

    public Subject setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Subject setName(String name) {
        this.name = name;
        return this;
    }

    public String getPredictedRating() {
        return predictedRating;
    }

    public Subject setPredictedRating(String predictedRating) {
        this.predictedRating = predictedRating;
        return this;
    }

    public String getFinalRating() {
        return finalRating;
    }

    public Subject setFinalRating(String finalRating) {
        this.finalRating = finalRating;
        return this;
    }
}
