package com.example.project_iot.objects;

import java.sql.Timestamp;

public class Alarm {

    static public enum Status {
        ACTIVE,
        SURPRESSED,
        ARCHIVED,
        EXPIRED,
    }

    private int id;
    private Status status;
    private String message;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
