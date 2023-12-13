package com.example.project_iot.database;

import com.example.project_iot.objects.Alarm;
import com.example.project_iot.objects.DeviceLog;
import com.example.project_iot.objects.User;
import com.example.project_iot.objects.devices.ADevice;

import java.sql.Timestamp;
import java.util.ArrayList;

public interface IDatabaseHelper {

    /**
     * Opens connection
     * @return true if connection established
     */
    public boolean open();

    /**
     * Closes connection
     */
    public void close();

    /**
     * Creates user
     * @param username
     * @param password
     * @return created user id
     */
    public int insert(String username, String password);

    /**
     * Checks if user exists
     * @param username
     * @return true if user already exists
     */
    public boolean isUsernameTaken(String username);

    /**
     * Checks user credentials
     * @param username
     * @param password
     * @return userId or -1 if not authorised
     */
    public int authoriseUser(String username, String password);

    /**
     * Gets list of user devices
     * @param userId
     * @return
     */
    public ArrayList<Integer> getUserDevicesIds(int userId);

    /**
     * Loads device
     * @param deviceId
     * @return device
     */
    public ADevice getDevice(int deviceId);

    /**
     * Updates device data
     * @param deviceId
     * @param * @param deviceId
     */
    public void updateDeviceName(int deviceId, String newName);

    /**
     * Updates device description
     * @param deviceId
     * @param newDescription
     */
    public void updateDeviceDescription(int deviceId, String newDescription);

    /**
     * Updates device location
     * @param deviceId
     * @param newLocation
     */
    public void updateDeviceLocation(int deviceId, String newLocation);

    /**
     * Updates device active status (active/not active)
     * @param deviceId
     * @param isActive
     */
    public void updateDeviceActiveStatus(int deviceId, boolean isActive);

    /**
     * get all alarms by user devices (user.getDevices())
     * @param user
     * @return
     */
    public ArrayList<Alarm> getAllAlarms(User user);

    /**
     * get active alarms by user devices (user.getDevices())
     * @param user
     * @return
     */
    public ArrayList<Alarm> getAlarmsWithStatus(int userId, String status);

    /**
     * Update alarm status info
     * @param alarmId
     * @param status
     */
    public void updateAlarmStatus(int alarmId, Alarm.Status status);

    /**
     * Get device ID and password by serial number
     * @param serialNumber
     * @return DevicePairingInfo
     */
    public MySQLDatabaseHelper.DevicePairingInfo getDevicePairingInfo(int serialNumber);

    /**
     * Gets latest data log from device
     * @param deviceId
     * @return DeviceLog
     */
    public DeviceLog getLatestDataLog(int deviceId);

}
