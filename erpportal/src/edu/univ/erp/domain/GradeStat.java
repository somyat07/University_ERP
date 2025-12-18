package edu.univ.erp.domain;

public class GradeStat {
    private final String component;
    private final double average;
    private final double maxScore;
    private final double minScore;
    private final int count;

    public GradeStat(String component, double average, double maxScore, double minScore, int count) {
        this.component = component;
        this.average = average;
        this.maxScore = maxScore;
        this.minScore = minScore;
        this.count = count;
    }

    public String getComponent() { return component; }
    public double getAverage() { return average; }
    public double getMaxScore() { return maxScore; }
    public double getMinScore() { return minScore; }
    public int getCount() { return count; }
}
