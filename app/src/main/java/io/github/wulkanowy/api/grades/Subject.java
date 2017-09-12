package io.github.wulkanowy.api.grades;

public class Subject {

    protected int id;

    protected String name;

    protected String predictedRating;

    protected String finalRating;

    public int getId() {
        return id;
    }

    public Subject setId(int id) {
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
