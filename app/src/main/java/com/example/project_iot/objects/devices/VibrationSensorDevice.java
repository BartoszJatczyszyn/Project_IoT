package com.example.project_iot.objects.devices;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

public class VibrationSensorDevice extends ADevice {

    private int threshold = 70;

    public VibrationSensorDevice() {
        super(Type.VIBRATE_SENSOR);
    }

    @Override
    public void loadSerializedAdditionalSettings(String json) {

        /*
            Due to Arduino limitations vibration threshold is not stored in json
         */

        this.threshold = Integer.valueOf(json);

        /*
        HashMap<String, Object> settings = new HashMap<>();
        settings.putAll(new Gson().fromJson(json, new TypeToken<HashMap<String, Object>>(){}.getType()));

        if (settings.containsKey("threshold")){
            threshold = (int) ((double) settings.get("threshold"));
        }
        */

    }

    @Override
    public String serializeAdditionalSettings() {

        /*
        HashMap<String, Object> settings = new HashMap<>();
        settings.put("threshold", threshold);
        return new Gson().toJson(settings);
        */
        return threshold + "";
    }

    /*
        Getters and setters
     */

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

}
