package com.example.project_iot.objects.devices;

public class MotionSensorDevice extends ADevice {
    public MotionSensorDevice() {
        super(Type.MOTION_SENSOR);
    }

    @Override
    public void loadSerializedAdditionalSettings(String json) {

    }

    @Override
    public String serializeAdditionalSettings() {
        return null;
    }
}
