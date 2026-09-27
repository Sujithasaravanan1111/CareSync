package com.caresync.dto;

public class MedicationAdherenceResponse {

    private int totalReminders;
    private int taken;
    private int missed;
    private int pending;
    private double adherencePercentage;
    private String insight;

    public MedicationAdherenceResponse() {
    }

    public int getTotalReminders() {
        return totalReminders;
    }

    public void setTotalReminders(int totalReminders) {
        this.totalReminders = totalReminders;
    }

    public int getTaken() {
        return taken;
    }

    public void setTaken(int taken) {
        this.taken = taken;
    }

    public int getMissed() {
        return missed;
    }

    public void setMissed(int missed) {
        this.missed = missed;
    }

    public int getPending() {
        return pending;
    }

    public void setPending(int pending) {
        this.pending = pending;
    }

    public double getAdherencePercentage() {
        return adherencePercentage;
    }

    public void setAdherencePercentage(double adherencePercentage) {
        this.adherencePercentage = adherencePercentage;
    }

    public String getInsight() {
        return insight;
    }

    public void setInsight(String insight) {
        this.insight = insight;
    }
}