package com.example.project_iot.objects.devices;

import java.sql.Timestamp;

public abstract class ADevice {

    static public enum Type {
        CAMERA,
        MOTION_SENSOR,
        VIBRATION_SENSOR,
        ;
    }

    private int id;

    private String Name;
    private String description;
    private Type type;
    private String location;
    private boolean active;
    private Timestamp insertDate;
    private Timestamp updateDate;


    public ADevice(Type type){
        this.type = type;
    }

    public abstract void loadSerializedAdditionalSettings(String json);

    public abstract String serializeAdditionalSettings();

    /*
        Usefull static methods
     */

    public static ADevice getDeviceInstanceByType(Type type) {

        if (type == ADevice.Type.VIBRATION_SENSOR){
            return new VibrationSensorDevice();
        } else if (type == Type.CAMERA){
            return new CameraDevice();
        } else if (type == Type.MOTION_SENSOR){
            return new MotionSensorDevice();
        }
        return null;
    }

    /*
        Getters and setters
     */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Timestamp getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Timestamp insertDate) {
        this.insertDate = insertDate;
    }

    public Timestamp getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Timestamp updateDate) {
        this.updateDate = updateDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
