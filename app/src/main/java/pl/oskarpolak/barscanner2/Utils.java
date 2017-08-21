package pl.oskarpolak.barscanner2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.text.IDNA;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pl.oskarpolak.barscanner2.data.Partia;
import pl.oskarpolak.barscanner2.data.Product;
import pl.oskarpolak.barscanner2.mysql.MysqlLocalConnector;

/**
 * Created by OskarPraca on 2016-12-12.
 */



public class Utils {

    public static final String DBNAME = "testowa";
    public  static final int VERSION = 6;

    public static String getLastTwoDigistsOfYear() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("yy"); // Just the year, with 2 digits
        return sdf.format(Calendar.getInstance().getTime());
    }

    public static String getData() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); // Just the year, with 2 digits
        return sdf.format(Calendar.getInstance().getTime());
    }

    public static String getDataShort() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Just the year, with 2 digits
        return sdf.format(Calendar.getInstance().getTime());
    }

    public static boolean checkWifiConnection(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            return true;
        }
        return false;
    }

    public static boolean isWindowOpened = false;
    public static AlertDialog dialog;
    public static void checkWifiConnectionWithMessage(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (!mWifi.isConnected()) {
            MysqlLocalConnector.getInstance(context).getConnection();
            if(!isWindowOpened) {
                dialog = createDialogWifi(context, "Błąd", "Brak połączenia wifi!");
                isWindowOpened = true;
            }
        }else{
            if(isWindowOpened){
                dialog.dismiss();
                isWindowOpened = false;
            }
        }
        if (!MysqlLocalConnector.getInstance(context).isDatabaseConnected()) {
            MysqlLocalConnector.getInstance(context).getConnection();
            createDialog(context, "Błąd", "Brak połączenia z bazą danych!");
        }
    }


    public static int getWIFIStrenght(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int numberOfLevels = 100;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return(WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels));
    }

    public static AlertDialog createDialogWifi(Context con, String name, String text){
        return new AlertDialog.Builder(con)
                .setTitle(name)
                .setMessage(text)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void createDialog(Context con, String name, String text){
        new AlertDialog.Builder(con)
                .setTitle(name)
                .setMessage(text)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        isWindowOpened = false;
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void showCustomInfoDialog(Context con, List<Product> productList, final int scroll) {
        final Dialog dialog = new Dialog(con);
        dialog.setContentView(R.layout.custom_info);
        dialog.setTitle("Stany magazynowe");

        // set the custom dialog components - text, image and button
        final ListView lista = (ListView) dialog.findViewById(R.id.listStanProdukt);
        lista.setAdapter(new InfoAdapter(productList, con));

        lista.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                lista.setSelection(scroll);
            }
        });





        Button dialogButton = (Button) dialog.findViewById(R.id.buttonExit);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void showCustomInfoDialogPartie(final NewDocumentActivity con, final Product productList) {
        final Dialog dialog = new Dialog(con);
        dialog.setContentView(R.layout.wybierz_partie);
        dialog.setTitle("Wybierz partię");

        // set the custom dialog components - text, image and button
        ListView lista = (ListView) dialog.findViewById(R.id.listaPartie);
        lista.setAdapter(new PartiaAdapter(productList, con));

        Button button = (Button) dialog.findViewById(R.id.buttonExitPartie);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                productList.setWybranaPartia(productList.getPartie().get(position));
                showCustomInfoDialogDostawy(con, productList);
                dialog.dismiss();
            }
        });


        dialog.show();
    }



    public static void showCustomInfoDialogDostawy(final NewDocumentActivity con, final Product p) {
        final Dialog dialog = new Dialog(con);
        dialog.setContentView(R.layout.custom_dostawa);
        dialog.setTitle("Wybierz dostawę");
        dialog.setCancelable(false);

        // set the custom dialog components - text, image and button
        ListView lista = (ListView) dialog.findViewById(R.id.listaPartie);
        lista.setAdapter(new DostawaAdapter(p, con));

        Button exit = (Button) dialog.findViewById(R.id.buttonExitDostawa);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(p.isHasPartion()){
                    showCustomInfoDialogPartie(con, p);
                    dialog.dismiss();
                }else {
                    dialog.dismiss();
                }



            }
        });


        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(p.isHasPartion()) {
                    p.setWybranaDostawa(p.getWybranaPartia().getPozcyje().get(position));
                    p.setWybranyZasob(p.getWybranaPartia().getZasoby().get(position));

                }else{
                    p.setWybranyZasob(p.getPartie().get(position).getZasoby().get(0));
                    p.setWybranaPartia(p.getPartie().get(position));
                    p.setWybranaDostawa(p.getPartie().get(position).getPozcyje().get(0));
                    Log.e("nowysystem", "Wybrano dostawe: " + p.getPartie().get(position).getPozcyje().get(0));
                }
                //Log.e("debug", "Wybrano dostawe: " + p.getWybranaPartia().getPozcyje().get(position));
                con.addProductToList(p);
                dialog.dismiss();
            }
        });


        dialog.show();
    }


    }


