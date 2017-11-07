package com.example.augusto.gps;


import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class BackgroundTask extends AsyncTask<String,Void,Void> {

    Context ctx;

    // STEP 1. Set database URL, credentials and names.
    private static final String PUBLIC_DNS = "sypy-db-instance.cjztblqral8m.us-east-2.rds.amazonaws.com";
    private static final String PORT = "10250";
    private static final String DATABASE = "sypydb";
    private static final String REMOTE_DATABASE_USERNAME = "sypy_design";
    private static final String DATABASE_USER_PASSWORD = "sypy_1234";
    private static final String TABLE_NAME = "localiz";

    // Table Names
    private static final String TABLE_VEHICLE_2 = "localiz2";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_LAT = "latitud";
    private static final String KEY_LON = "longitud";
    private static final String KEY_TIME = "tiempo";
    private static final String KEY_RPM = "rpm";

    private static final String CREATE_TABLE_VEHICLE_2 = "CREATE TABLE IF NOT EXISTS "
            + TABLE_VEHICLE_2 + " (" + KEY_ID + " INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT, " + KEY_LAT
            + " VARCHAR(15) NOT NULL, " + KEY_LON + " VARCHAR(15) NOT NULL, " + KEY_TIME
            + " VARCHAR(23) NOT NULL, " + KEY_RPM + " VARCHAR(15) NOT NULL" + ")";

    private Connection connection;

    BackgroundTask(Context ctx)
    {
        this.ctx=ctx;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        System.out.println("---- MariaDB JDBC Connection Testing -------");
        // STEP 2: Register JDBC driver and handle exception
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MariaDB JDBC Driver?");
            e.printStackTrace();

        }

        System.out.println("MariaDB JDBC Driver Registered!");

    }
    @Override
    protected Void doInBackground(String... params) {
        String lat = params[1];
        String lon = params[2];
        String tim = params[3];
        String rpm = params[4];
//        System.out.println("---- MariaDB JDBC Connection Testing -------");
//        // STEP 2: Register JDBC driver and handle exception
//        try {
//            Class.forName("org.mariadb.jdbc.Driver");
//        } catch (ClassNotFoundException e) {
//            System.out.println("Where is your MariaDB JDBC Driver?");
//            e.printStackTrace();
//            return null;
//        }
//
//        System.out.println("MariaDB JDBC Driver Registered!");
//        Connection connection = null;

        // STEP 3: Open a connection
        try {
            connection = DriverManager.
                    getConnection("jdbc:mariadb://" + PUBLIC_DNS + ":" + PORT + "/" + DATABASE, REMOTE_DATABASE_USERNAME, DATABASE_USER_PASSWORD);
            System.out.println("TEST" + connection);
        } catch (SQLException e) {
            System.out.println("Connection Failed!:\n" + e.getMessage());
        }
        Statement stat_table;
        if (connection != null) {
            System.out.println("SUCCESS!!!! You made it, take control your database now!");
            try {
                stat_table = connection.createStatement();
                stat_table.executeQuery(CREATE_TABLE_VEHICLE_2);
                writeQueryNewTable(connection,lat,lon,tim,rpm);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("FAILURE! Failed to make connection!");
        }

        return null;
    }

    @Override
    protected void  onProgressUpdate(Void... values){
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid){
        super.onPostExecute(aVoid);
    }

    private static void runTestQuery(Connection conn) {
        Statement statement = null;
        try {
            // STEP 4: Execute a test query on database
            System.out.println("Creating statement...");
            statement = conn.createStatement();
            String sql;
            sql = "SELECT * FROM " + TABLE_NAME;
            ResultSet rs = statement.executeQuery(sql);

            // STEP 5: Extract data from result set
            while (rs.next()) {
                //Retrieve by column name
                int id = rs.getInt("id");
                String latitud = rs.getString("latitud");
                String longitud = rs.getString("longitud");
                String tiempo = rs.getString("tiempo");

                //Display values
                System.out.print(" \n ID: " + id);
                System.out.print(", latitud: " + latitud);
                System.out.print(", longitud: " + longitud);
                System.out.print(", tiempo: " + tiempo);
            }
            // STEP 6: Clean-up environment
            rs.close();
            statement.close();
            conn.close();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException ignored) {

            }// nothing we can do
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
    }


    private static void writeQueryNewTable(Connection conn, String lat, String lon, String time, String rpm) {
        Statement statement = null;
        try {
            // STEP 4: Execute a test query on database
            System.out.println("Writing inside table " + TABLE_VEHICLE_2 + "from " + DATABASE  + " database.");
            statement = conn.createStatement();
            String sql;

            sql = "INSERT INTO " + TABLE_VEHICLE_2 + " (" + KEY_LAT + "," + KEY_LON + "," + KEY_TIME + "," + KEY_RPM + ") "
                    + "VALUES " + "('" + lat + "', '" + lon + "', '" + time + "', '" + rpm +"')";
            ResultSet rs = statement.executeQuery(sql);

            // STEP 6: Clean-up environment
            rs.close();
            statement.close();
            conn.close();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException ignored) {

            }// nothing we can do
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
    }

}