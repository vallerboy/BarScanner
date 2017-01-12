package pl.oskarpolak.barscanner2.mysql;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by OskarPraca on 2016-10-12.
 */

public class MysqlLocalConnector {

//    public static final String urlData = "jdbc:mysql://192.168.0.19:3306/bartek?useUnicode=yes&characterEncoding=UTF-8";
//    public static final String loginData = "root";
//    public static final String passwordData = "10135886";

    public static final String urlData = "jdbc:mysql://5.135.218.27:3306/oskar?useUnicode=yes&characterEncoding=UTF-8";
    public static final String loginData = "root";
    public static final String passwordData = "polako18";

    private static  MysqlLocalConnector state;
    private  static Connection connection;
    private static boolean isConnected;

    public Connection getConnection() {
        return connection;
    }

    private MysqlLocalConnector() {

    }

    public static MysqlLocalConnector getInstance() {
        if(state == null) {
             state = new MysqlLocalConnector();
             state.runDatabase();
             isConnected = false;
        }
        return state;
    }



    public  void runDatabase(){
        new AsyncMysql().execute();
    }


    public boolean isDatabaseConnected(){
        return isConnected;
    }




    private static class AsyncMysql extends AsyncTask<Void, Void, Connection>{
        @Override
        protected Connection doInBackground(Void... params) {

            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                Connection con = DriverManager.getConnection(urlData, loginData, passwordData);
                Log.e("Database", "It works!");
                isConnected = true;
                return con;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(@NonNull Connection aVoid) {
            connection = aVoid;
        }
    }

}
