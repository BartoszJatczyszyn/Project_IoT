package com.example.project_iot.objects.devices;

public class VibrationSensorDevice extends ADevice {

    private int vibration;

    public VibrationSensorDevice() {
        super(Type.VIBRATION_SENSOR);
    }

    @Override
    public void loadSerializedAdditionalSettings(String json) {

    }

    @Override
    public String serializeAdditionalSettings() {
        return null;
    }

}
