package pl.oskarpolak.barscanner2.mysql;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by OskarPraca on 2016-10-12.
 */

// Kartoteki, naprawa.
    // Marmande 2 zasoby, stokrotka
    // Info, listview, brakuje zasobow (nie zmieniaja sie nazwy) -- gotowe

public class MysqlLocalConnector {

    public static final String urlData = "jdbc:mysql://192.168.0.19:3306/aktualizacja?useUnicode=yes&characterEncoding=UTF-8";
    public static final String loginData = "root";
    public static final String passwordData = "10135886";


    static IntentFilter intentFilter = new IntentFilter();



//    public static final String urlData = "jdbc:mysql://5.135.218.27:3306/oskar?useUnicode=yes&characterEncoding=UTF-8";
//    public static final String loginData = "root";
//    public static final String passwordData = "polako18";

    private static  MysqlLocalConnector state;
    private  static Connection connection;
    private static boolean isConnected;


    public Connection getConnection() {
        try {
            if(connection == null || connection.isClosed()){
                state.runDatabase();
                isConnected = false;
                Log.e("baza", "łącze na nowo");
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    static Context context;
    static WifiManager wifi;

    private MysqlLocalConnector() {

    }

    public static MysqlLocalConnector getInstance(Context con) {
        if(state == null) {
             state = new MysqlLocalConnector();
             context = con;
             wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
             state.runDatabase();
             isConnected = false;
             isConnecting = true;
        }
        return state;
    }



    public  void runDatabase(){
        new AsyncMysql().execute();
    }


    public boolean isDatabaseConnected(){
        return isConnected;
    }




    static boolean isWindowOpened = false;
    static boolean isConnecting = false;
    static ProgressDialog progressDialog;
    private static class AsyncMysql extends AsyncTask<Void, Void, Connection>{




        @Override
        protected Connection doInBackground(Void... params) {

            try {
             //if (!isConnecting) {
                    Class.forName("com.mysql.jdbc.Driver").newInstance();
                    Connection con = DriverManager.getConnection(urlData, loginData, passwordData);
                    Log.e("Database", "It works!");
                    isConnected = true;
                    isConnecting = true;
                    return con;
              //  }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                Log.e("siec", "reconnect " + wifi.reconnect());
                Log.e("siec", "reconnect " + wifi.reassociate());
                publishProgress();
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            new  AsyncMysql().execute();
        }

        @Override
        protected void onPreExecute() {

            if(!isWindowOpened) {
//                Looper.prepare();
             //   progressDialog = new ProgressDialog(context);
             //   progressDialog.setTitle("Baza danych");
              //  progressDialog.setMessage("lacze z baza danych");
             //   progressDialog.setCancelable(false);
             //   progressDialog.show();
                isWindowOpened = true;
            }

         }

        @Override
        protected void onPostExecute(@NonNull Connection aVoid) {
            isWindowOpened = false;
            isConnecting = false;
           // progressDialog.dismiss();
            connection = aVoid;
        }
    }

}
