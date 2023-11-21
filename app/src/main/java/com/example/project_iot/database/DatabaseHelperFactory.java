package com.example.project_iot.database;

public class DatabaseHelperFactory {

    public static IDatabaseHelper getMysqlDatabase(){
        return new MySQLDatabaseHelper(new SQLConfig("20.107.176.118", 3306, "iot_project", "iot2023", "iot2023"));
    }

    public static IDatabaseHelper getMysqlDatabase(SQLConfig sqlConfig){
        return new MySQLDatabaseHelper(sqlConfig);
    }

}
