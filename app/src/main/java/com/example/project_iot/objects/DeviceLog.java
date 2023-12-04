package com.example.project_iot.objects;

import java.sql.Timestamp;

public class DeviceLog {

    public enum Type {
        CRITICAL,
        WARNING,
        ERROR,
        INFO,
        TEMP
    }

    private Type logType;
    private String logContent;


    private Timestamp insertDate;

    public DeviceLog(Type logType, String logContent, Timestamp insertDate) {
        this.logType = logType;
        this.logContent = logContent;
        this.insertDate = insertDate;
    }

    /*
        Getters and setters
     */

    public Type getLogType() {
        return logType;
    }

    public void setLogType(Type logType) {
        this.logType = logType;
    }

    public String getLogContent() {
        return logContent;
    }

    public void setLogContent(String logContent) {
        this.logContent = logContent;
    }

    public Timestamp getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Timestamp insertDate) {
        this.insertDate = insertDate;
    }
}
