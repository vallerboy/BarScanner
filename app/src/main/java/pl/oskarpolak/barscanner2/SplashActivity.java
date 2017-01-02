package pl.oskarpolak.barscanner2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.oskarpolak.barscanner2.mysql.MysqlLocalConnector;

public class SplashActivity extends AppCompatActivity {


    @BindView(R.id.progressSplash)
    ProgressBar progressBar;

    @BindView(R.id.textSplash)
    TextView textSplash;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        progressBar.setIndeterminate(true);
        new AsyncLoad().execute();
    }


    private class AsyncLoad extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            MysqlLocalConnector.getInstance();
            for (int i = 0; i <= 10; i++) {
                try {
                    if(i == 5) {
                        if(!Utils.checkWifiConnection(SplashActivity.this)) {
                            Utils.createDialog(SplashActivity.this, "Połączenie WiFi", "Aby aplikacja zadziałała, połącz się z siecią firmową");
                        }
                    }else if(i == 7) {

                    }
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress(i);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer  ... values) {
           switch (values[0]) {
               case 5:
                   textSplash.setText("Sprawdzam połączenie..");
                   break;
               case 7:
                   textSplash.setText("Próba połączenia z bazą danych..");
                   break;
               case 9:
                   textSplash.setText("Zamykanie bufforów..");
                   break;
           }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    }
}
