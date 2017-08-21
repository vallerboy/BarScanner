package pl.oskarpolak.barscanner2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.widget.Toast;

/**
 * Created by OskarPraca on 2017-01-24.
 */

public class WifiBroadcast extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

                Toast.makeText(context, "Zmiana WIFI", Toast.LENGTH_LONG).show();
                wifiManager.reassociate();


    }
}
