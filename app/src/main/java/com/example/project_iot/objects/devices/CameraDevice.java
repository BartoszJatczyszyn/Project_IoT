package com.example.project_iot.objects.devices;

public class CameraDevice extends ADevice {
    public CameraDevice() {
        super(Type.CAMERA);
    }

    @Override
    public void loadSerializedAdditionalSettings(String json) {

    }

    @Override
    public String serializeAdditionalSettings() {
        return null;
    }
}
