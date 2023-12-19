package com.example.project_iot.database;

import android.util.Log;

import com.example.project_iot.objects.Alarm;
import com.example.project_iot.objects.DeviceLog;
import com.example.project_iot.objects.User;
import com.example.project_iot.objects.devices.ADevice;
import com.example.project_iot.objects.devices.VibrationSensorDevice;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

public class MySQLDatabaseHelper implements IDatabaseHelper {
    private static String LOG_TAG = "MySQLDatabaseHelper";
    private static String USERS_TABLE = "users";
    private static String DEVICE_TABLE = "devices";
    private static String ALARMS_TABLE = "alarms";
    private static String LOGS_TABLE = "logs";
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

        boolean state = false;

        try {

            //Class.forName("com.mysql.cj.jdbc.Driver");
            Class.forName("com.mysql.jdbc.Driver");

            String DB_URL = "jdbc:mysql://"+sqlConfig.getDbHost()+":"+sqlConfig.getDbPort()+"/"+this.sqlConfig.getDbName()+"?autoReconnect=true";

            Log.d(LOG_TAG, DB_URL);

            conn = DriverManager.getConnection(DB_URL, sqlConfig.getDbUser(), sqlConfig.getDbPass());

            stat = conn.createStatement();
            stat.execute("SELECT 1 FROM " + USERS_TABLE);

            state = true;

        } catch (SQLException | ClassNotFoundException ex) {
            Log.e(LOG_TAG, Log.getStackTraceString(ex));
        } finally {
            this.close(stat);
        }

        return state;

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

            stat = conn.prepareStatement("SELECT * FROM " + USERS_TABLE + " WHERE user_name = ?");
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
     * @return userId or -1 if not authorised
     */
    public int authoriseUser(String username, String password) {
        PreparedStatement stat = null;
        ResultSet res = null;

        int userId = -1;

        try {

            stat = conn.prepareStatement("SELECT * FROM " + USERS_TABLE + " WHERE user_name = ? AND user_password = ?");
            stat.setString(1, username);
            stat.setString(2, password);

            res = stat.executeQuery();

            if (res.next()) {
                userId = res.getInt("id_user");
            }

        } catch (SQLException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        } finally {
            this.close(stat, res);
        }

        return userId;
    }

    /**
     * Gets list of user devices
     *
     * @param userId
     * @return
     */
    @Override
    public ArrayList<Integer> getUserDevicesIds(int userId) {

        PreparedStatement stat = null;
        ResultSet res = null;

        ArrayList<Integer> devicesIds = new ArrayList<Integer>();

        try {
            stat = conn.prepareStatement(" SELECT * FROM " + USERS_TABLE + " WHERE id_user = ?");
            stat.setInt(1, userId);
            res = stat.executeQuery();

            if (res.next()) {
                String userDevicesJson = res.getString("user_devices");
                if (userDevicesJson != null && !userDevicesJson.isEmpty()){
                    devicesIds = new Gson().fromJson(userDevicesJson, new TypeToken<ArrayList<Integer>>(){}.getType());
                }

            } else {
                throw new SQLException("Getting device failed, no devices retrieved.");
            }

        } catch (SQLException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        } finally {
            this.close(stat, res);
        }

        return devicesIds;
    }

    public void updateUserDevicesIds(int userId, ArrayList<Integer> deviceIds) {
        PreparedStatement stat = null;

        try {
            stat = conn.prepareStatement("UPDATE " + USERS_TABLE + " SET user_devices = ? WHERE id_user = ?");
            stat.setString(1, new Gson().toJson(deviceIds));
            stat.setInt(2, userId);
            stat.executeUpdate();
        } catch (SQLException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        } finally {
            this.close(stat, null);
        }
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

        ADevice device = null;

        try {
            stat = conn.prepareStatement(" SELECT * FROM " + DEVICE_TABLE + " WHERE id_device = ?");
            stat.setInt(1,deviceId);
            res = stat.executeQuery();

            if (res.next()) {
                device = loadDevice(res);
            } else {
                throw new SQLException("Getting device failed, no device retrieved.");
            }

        } catch (SQLException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        } finally {
            this.close(stat, res);
        }

        return device;
    }

    private ADevice loadDevice(ResultSet res) throws SQLException {

        ADevice device;

        ADevice.Type type = ADevice.Type.valueOf(res.getString("device_type").toUpperCase());

        device = ADevice.getDeviceInstanceByType(type);
        device.setId(res.getInt("id_device"));
        device.setName(res.getString("device_name"));
        device.setDescription(res.getString("device_desc"));
        device.setActive(res.getBoolean("active"));
        device.setLocation(res.getString("location"));
        device.setInsertDate(res.getTimestamp("insert_date"));
        device.setUpdateDate(res.getTimestamp("update_date"));
        device.loadSerializedAdditionalSettings(res.getString("device_attributes"));

        return device;
    }

    /**
     * Updates device data
     *
     * @param deviceId
     * @param newName
     */
    @Override
    public void updateDeviceName(int deviceId, String newName) {
        PreparedStatement stat = null;

        try {
            stat = conn.prepareStatement("UPDATE " + DEVICE_TABLE + " SET device_name = ? WHERE id_device = ?");
            stat.setString(1,newName);
            stat.setInt(2,deviceId);
            stat.executeUpdate();
        } catch (SQLException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        } finally {
            this.close(stat, null);
        }
    }

    /**
     * Updates device description
     *
     * @param deviceId
     * @param newDescription
     */
    @Override
    public void updateDeviceDescription(int deviceId, String newDescription) {
        PreparedStatement stat = null;
        ResultSet res = null;

        try {
            stat = conn.prepareStatement("UPDATE " + DEVICE_TABLE + " SET device_desc = ? WHERE id_device = ?");
            stat.setString(1,newDescription);
            stat.setInt(2,deviceId);
            stat.executeUpdate();
        } catch (SQLException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        } finally {
            this.close(stat, res);
        }
    }

    /**
     * Updates device location
     *
     * @param deviceId
     * @param newLocation
     */
    @Override
    public void updateDeviceLocation(int deviceId, String newLocation) {
        PreparedStatement stat = null;
        ResultSet res = null;

        try {
            stat = conn.prepareStatement("UPDATE " + DEVICE_TABLE + " SET location = ? WHERE id_device = ?");
            stat.setString(1,newLocation);
            stat.setInt(2,deviceId);
            stat.executeUpdate();
        } catch (SQLException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        } finally {
            this.close(stat, res);
        }
    }

    /**
     * Updates device active status (active/not active)
     *
     * @param deviceId
     * @param isActive
     */
    @Override
    public void updateDeviceActiveStatus(int deviceId, boolean isActive) {
        PreparedStatement stat = null;
        ResultSet res = null;

        int activity = 0;
        if (isActive){
            activity = 1;
        }

        try {
            stat = conn.prepareStatement("UPDATE " + DEVICE_TABLE + " SET active = ? WHERE id_device = ?");
            stat.setInt(1,activity);
            stat.setInt(2,deviceId);
            stat.executeUpdate();
        } catch (SQLException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        } finally {
            this.close(stat, res);
        }
    }

    /**
     * get all alarms by user devices (user.getDevices())
     *
     * @param userId
     * @return
     */
    @Override
    public ArrayList<Alarm> getAllAlarms(int userId) {
        PreparedStatement stat = null;
        ResultSet res = null;

        ArrayList<Alarm> alarms = new ArrayList<>();
        try {
            stat = conn.prepareStatement(" SELECT * FROM " + ALARMS_TABLE + " WHERE id_user = ?");
            stat.setInt(1, userId);
            res = stat.executeQuery();

            while (res.next()) {
                Alarm alarm = new Alarm();
                alarm.setStatus(Alarm.Status.valueOf(res.getString("alarm_status").toUpperCase()));
                alarm.setMessage(res.getString("alarm_message"));
                alarm.setInsertDate(res.getTimestamp("insert_date"));
                alarm.setUpdateDate(res.getTimestamp("update_date"));
                alarm.setId(res.getInt("id_alarm"));
                alarms.add(alarm);
            }
            return alarms;
        } catch (SQLException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        } finally {
            this.close(stat, res);
        }
        return null;
    }

    /**
     * get active alarms by user devices (user.getDevices())
     *
     * @param userId
     * @param status
     * @return
     */
    @Override
    public ArrayList<Alarm> getAlarmsWithStatus(int userId, String status) {
        PreparedStatement stat = null;
        ResultSet res = null;

        ArrayList<Alarm> alarms = new ArrayList<>();
        try {
            stat = conn.prepareStatement(" SELECT * FROM " + ALARMS_TABLE + " WHERE id_user = ? AND alarm_status = ? ");
            stat.setInt(1, userId);
            stat.setString(2,status);
            res = stat.executeQuery();

            while (res.next()) {
                Alarm alarm = new Alarm();
                alarm.setStatus(Alarm.Status.valueOf(res.getString("alarm_status").toUpperCase()));
                alarm.setMessage(res.getString("alarm_message"));
                alarm.setInsertDate(res.getTimestamp("insert_date"));
                alarm.setUpdateDate(res.getTimestamp("update_date"));
                alarm.setId(res.getInt("id_alarm"));
                alarms.add(alarm);
            }
            return alarms;
        } catch (SQLException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        } finally {
            this.close(stat, res);
        }
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
        PreparedStatement stat = null;
        ResultSet res = null;

        try {
            stat = conn.prepareStatement("UPDATE " + ALARMS_TABLE + " SET alarm_status = ? WHERE id_alarm = ?");
            stat.setString(1,status.toString().toLowerCase());
            stat.setInt(2,alarmId);
            stat.executeUpdate();
        } catch (SQLException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        } finally {
            this.close(stat, res);
        }
    }

    /**
     * Get device ID and password by serial number
     *
     * @param serialNumber
     * @return DevicePairingInfo
     */
    @Override
    public DevicePairingInfo getDevicePairingInfo(int serialNumber) {

        PreparedStatement stat = null;
        ResultSet res = null;

        try {
            stat = conn.prepareStatement(" SELECT * FROM " + DEVICE_TABLE + " WHERE serial_number = ?");
            stat.setInt(1,serialNumber);
            res = stat.executeQuery();

            if (res.next()) {
                String password = res.getString("device_password");
                int deviceId = res.getInt("id_device");
                return new DevicePairingInfo(deviceId,password);
            } else {
                throw new SQLException("Getting device pairing info failed, no pairing info retrieved.");
            }

        } catch (SQLException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        } finally {
            this.close(stat, res);
        }

        return null;
    }

    /**
     * Gets latest data log from device
     *
     * @param deviceId
     * @return DeviceLog
     */
    @Override
    public DeviceLog getLatestDataLog(int deviceId) {

        PreparedStatement stat = null;
        ResultSet res = null;

        try {
            stat = conn.prepareStatement("SELECT * FROM " + LOGS_TABLE + " WHERE id_device = ? ORDER BY insert_date DESC LIMIT 1");
            stat.setInt(1,deviceId);
            res = stat.executeQuery();

            if (res.next()) {
                String logType = res.getString("log_type");
                String logContent = res.getString("log_content");
                Timestamp timestamp = res.getTimestamp("insert_date");
                return new DeviceLog(DeviceLog.Type.valueOf(logType.toUpperCase()),logContent,timestamp);
            } else {
                throw new SQLException("Getting device failed, no data log retrieved.");
            }

        } catch (SQLException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        } finally {
            this.close(stat, res);
        }


        return null;
    }

    @Override
    public boolean isPasswordCorrect(int id_user, String password) {
        PreparedStatement stat = null;
        ResultSet res = null;

        boolean result = false;

        try {

            stat = conn.prepareStatement("SELECT * FROM " + USERS_TABLE + " WHERE id_user = ? AND user_password = ?");
            stat.setInt(1, id_user);
            stat.setString(2, password);

            res = stat.executeQuery();

            if (res.next()) {
                result = true;
            }

        } catch (SQLException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        } finally {
            this.close(stat, res);
        }

        return result;
    }

    @Override
    public int resetPassword(int id_user, String password){
        PreparedStatement stat = null;
        ResultSet res = null;
        int affectedRows = 0;
        try {

            stat = conn.prepareStatement("UPDATE " + USERS_TABLE + " SET user_password = ? WHERE id_user = ?", Statement.RETURN_GENERATED_KEYS);
            stat.setString(1, password);
            stat.setInt(2, id_user);

            stat.executeUpdate();

            affectedRows = stat.executeUpdate();
        } catch (SQLException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
            return affectedRows;
        } finally {
            this.close(stat, res);
        }
        return affectedRows;
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

    public class DevicePairingInfo {

        private int deviceId;
        private String password;

        public DevicePairingInfo(int deviceId, String password) {
            this.deviceId = deviceId;
            this.password = password;
        }

        public int getDeviceId() {
            return deviceId;
        }

        public String getPassword() {
            return password;
        }
    }
}