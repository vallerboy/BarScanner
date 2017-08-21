package pl.oskarpolak.barscanner2;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.jb.barcode.BarcodeManager;
import android.jb.utils.Tools;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;


import com.mysql.jdbc.Statement;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.oskarpolak.barscanner2.mysql.MysqlLocalConnector;

public class MainActivity extends AppCompatActivity{

    @BindView(R.id.textResult)
    TextView textResult;


    IntentFilter intentFilter = new IntentFilter();


    BarcodeManager barcodeManager;


     BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
         @Override
         public void onReceive(Context context, Intent intent) {
             final String action = intent.getAction();
             if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
                 if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)){
                     // wifiManager.reconnect();
                     Toast.makeText(context, "Zmiana WIFI", Toast.LENGTH_LONG).show();
                     wifiManager.reassociate();
                 } else {
                     // wifi connection was lost
                 }
             }
         }
     };


    WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

       ////  wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
       // intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
       // registerReceiver(broadcastReceiver, intentFilter);

        barcodeManager = BarcodeManager.getInstance();
        barcodeManager.Barcode_Open(this, dataReceived);
        barcodeManager.setScanTime(3000);


    }

    @OnClick(R.id.buttonScan)
    public void buttonStart(){
        if(MysqlLocalConnector.getInstance(this).isDatabaseConnected()) {
            Intent i = new Intent(this, NewDocumentActivity.class);
            startActivity(i);
        }else {
           Utils.createDialog(this, "Baza danych", "Baza danych nie jest jeszcze gotowa, poczekaj.");
        }
    }

    @OnClick(R.id.buttonReset)
    public void resetStan() {
        if(MysqlLocalConnector.getInstance(this).isDatabaseConnected()) {
            new AsyncReset().execute();
        }else{
            Utils.createDialog(this, "Baza danych", "Baza danych nie jest jeszcze gotowa, poczekaj.");

        }
    }


    @OnClick(R.id.buttonCena)
    public void sprawdzCene() {
        new MakeLoad().execute(barcodeManager);
    }



    private class loadCena extends  AsyncTask<String, Void, List<String>>{
        List<String> listCen = new ArrayList<String>();
        @Override
        protected List<String> doInBackground(String... params) {
            try {
                java.sql.Statement statementProdukt = MysqlLocalConnector.getInstance(MainActivity.this).getConnection().createStatement();
                java.sql.Statement statementCena = MysqlLocalConnector.getInstance(MainActivity.this).getConnection().createStatement();
                java.sql.Statement statementZasoby = MysqlLocalConnector.getInstance(MainActivity.this).getConnection().createStatement();

                ResultSet towar  = statementProdukt.executeQuery("SELECT * FROM Towary WHERE EAN=" + params[0] + ";");
                while(towar.next()){
                    ResultSet cena  = statementCena.executeQuery("SELECT * FROM Ceny WHERE Towar=" + towar.getString("ID") + ";");

                    String nazwa = towar.getString("Nazwa");
                      String detaliczna = "Detaliczna: ";
                      String hurtowa = "Hurtowa: ";
                      while(cena.next()){
                          if(cena.getString("Definicja").equals("3")){
                              detaliczna += cena.getString("NettoValue") + " / " + cena.getString("BruttoValue");
                          }else if(cena.getString("Definicja").equals("2")){
                              hurtowa += cena.getString("NettoValue") + " / " + cena.getString("BruttoValue");
                          }
                      }

                     ResultSet zasoby = statementZasoby.executeQuery("SELECT * FROM Zasoby WHERE Towar = " + towar.getString("Id") + " AND Okres = 1");
                     String ilosc = "Całość zasobu: ";
                    int zasobyInt = 0;
                    while(zasoby.next()) {
                             zasobyInt += zasoby.getInt("IloscValue");
                     }
                    ilosc += zasobyInt;



                   listCen.add(nazwa + "@" + detaliczna + "@" + hurtowa + "@" + ilosc);
                }


            } catch (SQLException e) {
                e.printStackTrace();
            }

            return listCen;
        }

        @Override
        protected void onPostExecute(List<String> list) {
            if(list.size() > 1){
                Toast.makeText(MainActivity.this, "Znalazłem 2 kartoteki", Toast.LENGTH_LONG).show();
            }
           for(String s : list) {
               String[] dane = s.split("@");
               Utils.createDialog(MainActivity.this, "Cena: " + dane[0], dane[1] + "\n" + dane[2] + "\n" + dane[3]);
           }
           }
    }


    public void result(String content) {

         new loadCena().execute(content);

    }

    BarcodeManager.Callback dataReceived = new BarcodeManager.Callback() {

        @Override
        public void Barcode_Read(byte[] buffer, String codeId, int errorCode) {
            String codeType = Tools.returnType(buffer);
            String val = null;
            if (codeType.equals("default")) {
                val = new String(buffer);
            } else {
                try {
                    val = new String(buffer, codeType);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("val:" + val);
            result(val);
        }
    };


    private class MakeLoad extends AsyncTask<BarcodeManager, Void, Void> {
        @Override
        protected Void doInBackground(BarcodeManager... params) {


            params[0].Barcode_Start();

            return null;
        }
    }



    private class AsyncReset extends AsyncTask<Void, Void, Void>{

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
             progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Resetuje stany");
            progressDialog.setTitle("Reset stanów");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {


            try {
                java.sql.Statement statement =  MysqlLocalConnector.getInstance(MainActivity.this).getConnection().createStatement();
                statement.execute("UPDATE TowaryCloud SET iloscSciagnieta = 0 WHERE iloscSciagnieta != 0");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }


    }








}
