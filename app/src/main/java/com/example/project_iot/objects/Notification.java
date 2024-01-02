package com.example.project_iot.objects;

import java.sql.Timestamp;

public class Notification {

    static public enum Type {
        INFO,
        CRITICAL,
        WARNING,
        ERROR;
    }

    static public enum SummaryType {
        DAILY,
        WEEKLY,
        MONTHLY,
        NA;
    }

    private int id;
    private Type type;
    private SummaryType summaryType;
    private String content;
    private boolean confirmed;
    private Timestamp updateDate;
    private Timestamp insertDate;

    /*
        Getters and setters
     */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public SummaryType getSummaryType() {
        return summaryType;
    }

    public void setSummaryType(SummaryType summaryType) {
        this.summaryType = summaryType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public Timestamp getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Timestamp updateDate) {
        this.updateDate = updateDate;
    }

    public Timestamp getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Timestamp insertDate) {
        this.insertDate = insertDate;
    }
}
