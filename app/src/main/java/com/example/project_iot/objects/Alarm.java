package com.example.project_iot.objects;

import com.example.project_iot.objects.devices.ADevice;

import java.sql.Timestamp;

public class Alarm implements Comparable {

    @Override
    public int compareTo(Object o) {

        Alarm a = (Alarm) o;

        if (this.getInsertDate().getTime() == a.getInsertDate().getTime())
            return 0;

        return a.getInsertDate().after(this.getInsertDate()) ? 1 : -1;
    }

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
    private ADevice device;

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

        if (message == null) return "Błąd!";

        if (message.contains("Utracono aktywność urządzenia"))
            return device.getName() + ": " + message;

        if (device.getType() == ADevice.Type.VIBRATE_SENSOR)
            return device.getName() + ": Wibracje powyżej thresholdu! (" + message + ")";
        else if (device.getType() == ADevice.Type.MOTION_SENSOR)
            return device.getName() + ": Wykryto ruch!";
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

    public ADevice getDevice() {
        return device;
    }

    public void setDevice(ADevice device) {
        this.device = device;
    }
}
