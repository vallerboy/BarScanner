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

import pl.oskarpolak.barscanner2.data.Product;
import pl.oskarpolak.barscanner2.mysql.MysqlLocalConnector;

/**
 * Created by OskarPraca on 2016-12-12.
 */



public class Utils {

    public static final String DBNAME = "testowa";
    public  static final int VERSION = 3;

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

    public static void checkWifiConnectionWithMessage(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (!mWifi.isConnected()) {
            createDialog(context, "Błąd", "Brak połączenia wifi!");
        }
        if (!MysqlLocalConnector.getInstance().isDatabaseConnected()) {
            createDialog(context, "Błąd", "Brak połączenia z bazą danych!");
        }
    }


    public static int getWIFIStrenght(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int numberOfLevels = 100;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return(WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels));
    }

    public static void createDialog(Context con, String name, String text){
        new AlertDialog.Builder(con)
                .setTitle(name)
                .setMessage(text)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static void showCustomInfoDialog(Context con, List<Product> productList) {
        final Dialog dialog = new Dialog(con);
        dialog.setContentView(R.layout.custom_info);
        dialog.setTitle("Stany magazynowe");

        // set the custom dialog components - text, image and button
        final ListView lista = (ListView) dialog.findViewById(R.id.listStanProdukt);
        lista.setAdapter(new InfoAdapter(productList, con));

        Button dialogRefresh = (Button) dialog.findViewById(R.id.buttonRefresh);
        dialogRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoAdapter infoAdapter = (InfoAdapter) lista.getAdapter();
                infoAdapter.refresh();
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

    public static void showCustomInfoDialogPartie(final NewDocumentActivity con, final List<Product> productList) {
        final Dialog dialog = new Dialog(con);
        dialog.setContentView(R.layout.wybierz_partie);
        dialog.setTitle("Wybierz partię");

        // set the custom dialog components - text, image and button
        ListView lista = (ListView) dialog.findViewById(R.id.listaPartie);
        lista.setAdapter(new PartiaAdapter(productList, con));



        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for(String s : productList.get(position).getDoce()){
                    Log.e("partie", "doce partii dla wybranego produktu: " + s);
                }
                showCustomInfoDialogDostawy(con, productList.get(position));
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    public static void showCustomInfoDialogDostawy(final NewDocumentActivity con, final Product p) {
        final Dialog dialog = new Dialog(con);
        dialog.setContentView(R.layout.wybierz_partie);
        dialog.setTitle("Wybierz dostawę");
        dialog.setCancelable(false);

        // set the custom dialog components - text, image and button
        ListView lista = (ListView) dialog.findViewById(R.id.listaPartie);
        lista.setAdapter(new DostawaAdapter(p, con));



        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                p.setWybranaDostawa(p.getDoce().get(position));
                Log.e("debug", "Wybrano dostawe: " + p.getDoce().get(position));
                con.addProductToList(p);
                dialog.dismiss();
            }
        });


        dialog.show();
    }


    }


