package com.example.project_iot.objects;

import com.example.project_iot.objects.devices.ADevice;

import java.util.ArrayList;

public class User {

    private int id;
    private String userName;

    private ArrayList<ADevice> devices = new ArrayList<ADevice>();

    public void loadDevices(String json){
        //TODO: load devices from json array containing ids
    }

    public String getDevicesListJson() {
        //TODO: serialize services ids to json array
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ArrayList<ADevice> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<ADevice> devices) {
        this.devices = devices;
    }
}
