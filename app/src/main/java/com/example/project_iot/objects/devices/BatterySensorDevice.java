package com.example.project_iot.objects.devices;

public class BatterySensorDevice extends ADevice {
    public BatterySensorDevice() {
        super(Type.BATTERY_SENSOR);
    }

    @Override
    public void loadSerializedAdditionalSettings(String json) {

    }

    @Override
    public String serializeAdditionalSettings() {
        return null;
    }
}
