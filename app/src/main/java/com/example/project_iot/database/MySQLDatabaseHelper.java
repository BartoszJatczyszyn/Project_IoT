package com.example.project_iot.database;

import android.util.Log;

import com.example.project_iot.objects.Alarm;
import com.example.project_iot.objects.User;
import com.example.project_iot.objects.devices.ADevice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MySQLDatabaseHelper implements IDatabaseHelper {
    private static String LOG_TAG = "MySQLDatabaseHelper";
    private static String USERS_TABLE = "users";
    private static String DEVICE_TABLE = "devices";
    private SQLConfig sqlConfig;

    private Connection conn;

    public MySQLDatabaseHelper(SQLConfig sqlConfig){
        this.sqlConfig = sqlConfig;
    }

    /**
     * Opens connection
     * @return true if connection established
     */
    @Override
    public boolean open() {

        Statement stat = null;

        try {

            //Class.forName("com.mysql.cj.jdbc.Driver");
            Class.forName("com.mysql.jdbc.Driver");

            String DB_URL = "jdbc:mysql://"+sqlConfig.getDbHost()+":"+sqlConfig.getDbPort()+"/"+this.sqlConfig.getDbName()+"?autoReconnect=true";

            Log.d(LOG_TAG, DB_URL);

            conn = DriverManager.getConnection(DB_URL, sqlConfig.getDbUser(), sqlConfig.getDbPass());

            stat = conn.createStatement();
            stat.execute("SELECT 1 FROM " + USERS_TABLE);

            return true;

        } catch (SQLException | ClassNotFoundException ex) {
            Log.e(LOG_TAG, Log.getStackTraceString(ex));
            return false;
        } finally {
            this.close(stat);
        }


    }

    /**
     * Closes connection
     */
    @Override
    public void close() {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException ex) {
            Log.e(LOG_TAG, Log.getStackTraceString(ex));
        }
    }

    /**
     * Creates user
     * @param username
     * @param password
     * @return created user id
     */
    @Override
    public int insert(String username, String password) {

        PreparedStatement stat = null;
        ResultSet res = null;

        int userId = -1;

        try {

            stat = conn.prepareStatement("INSERT INTO " + USERS_TABLE + " (user_name, user_password, active) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS);
            stat.setString(1, username);
            stat.setString(2, password);
            stat.setInt(3, 1);

            stat.executeUpdate();

            res = stat.getGeneratedKeys();
            if (res.next()) {
                userId = res.getInt(1);
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }

        } catch (SQLException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        } finally {
            this.close(stat, res);
        }

        return userId;
    }

    /**
     * Checks if user exists
     * @param username
     * @return true if user already exists
     */
    @Override
    public boolean isUsernameTaken(String username) {

        PreparedStatement stat = null;
        ResultSet res = null;

        boolean exists = false;

        try {

            stat = conn.prepareStatement("SELECT * FROM " + USERS_TABLE + " WHERE user_name = ?", Statement.RETURN_GENERATED_KEYS);
            stat.setString(1, username);

            res = stat.executeQuery();

           if (res.next()) {
               exists = true;
           }

        } catch (SQLException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        } finally {
            this.close(stat, res);
        }

        return exists;

    }

    /**
     * Checks user credentials
     * @param username
     * @param password
     * @return true if user credentials are correct
     */
    @Override
    public boolean checkLogin(String username, String password) {
        PreparedStatement stat = null;
        ResultSet res = null;

        boolean correct = false;

        try {

            stat = conn.prepareStatement("SELECT * FROM " + USERS_TABLE + " WHERE user_name = ? AND user_password = ?", Statement.RETURN_GENERATED_KEYS);
            stat.setString(1, username);
            stat.setString(2, password);

            res = stat.executeQuery();

            if (res.next()) {
                correct = true;
            }

        } catch (SQLException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        } finally {
            this.close(stat, res);
        }

        return correct;
    }

    /**
     * Loads device
     *
     * @param deviceId
     * @return device
     */
    @Override
    public ADevice getDevice(int deviceId) {

        PreparedStatement stat = null;
        ResultSet res = null;

        try {
            stat = conn.prepareStatement(" SELECT * FROM " + DEVICE_TABLE + " WHERE id_device = ?", Statement.RETURN_GENERATED_KEYS);
            stat.setInt(1,deviceId);
            res = stat.executeQuery();

            if (res.next()) {
                String deviceType = res.getString("device_type");
                return new ADevice(ADevice.Type.valueOf(deviceType.toUpperCase())) {
                    @Override
                    public void loadSerializedAdditionalSettings(String json) {
                    }
                    @Override
                    public String serializeAdditionalSettings() {
                        return null;
                    }
                };
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }

        } catch (SQLException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        } finally {
            this.close(stat, res);
        }

        return null;
    }

    /**
     * Updates device data
     *
     * @param deviceId
     * @param newName
     */
    @Override
    public void updateDeviceName(int deviceId, String newName) {

    }

    /**
     * Updates device description
     *
     * @param deviceId
     * @param newDescription
     */
    @Override
    public void updateDeviceDescription(int deviceId, String newDescription) {
    }

    /**
     * Updates device location
     *
     * @param deviceId
     * @param newLocation
     */
    @Override
    public void updateDeviceLocation(int deviceId, String newLocation) {

    }

    /**
     * Updates device active status (active/not active)
     *
     * @param deviceId
     * @param isActive
     */
    @Override
    public void updateDeviceActiveStatus(int deviceId, boolean isActive) {

    }

    /**
     * get all alarms by user devices (user.getDevices())
     *
     * @param user
     * @return
     */
    @Override
    public ArrayList<Alarm> getAllAlarms(User user) {
        return null;
    }

    /**
     * get active alarms by user devices (user.getDevices())
     *
     * @param user
     * @return
     */
    @Override
    public ArrayList<Alarm> getActiveAlarms(User user) {
        return null;
    }

    /**
     * Update alarm status info
     *
     * @param alarmId
     * @param status
     */
    @Override
    public void updateAlarmStatus(int alarmId, Alarm.Status status) {

    }

    private void close(Statement stat) {
        this.close(stat, null);
    }
    private void close(Statement stat, ResultSet res) {
        try {
            if (stat != null && !stat.isClosed()) stat.close();
            if (res != null && !res.isClosed()) res.close();
        } catch (SQLException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        }
    }
}