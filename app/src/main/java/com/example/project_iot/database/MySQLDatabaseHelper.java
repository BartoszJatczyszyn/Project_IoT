package com.example.project_iot.database;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLDatabaseHelper implements IDatabaseHelper {
    private static String LOG_TAG = "MySQLDatabaseHelper";
    private static String USERS_TABLE = "users";
    private SQLConfig sqlConfig;

    private Connection conn;

    public MySQLDatabaseHelper(SQLConfig sqlConfig){
        this.sqlConfig = sqlConfig;
    }

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

    @Override
    public void close() {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException ex) {
            Log.e(LOG_TAG, Log.getStackTraceString(ex));
        }
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
}