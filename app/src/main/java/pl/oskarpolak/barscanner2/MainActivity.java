package pl.oskarpolak.barscanner2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.oskarpolak.barscanner2.mysql.MysqlLocalConnector;

public class MainActivity extends AppCompatActivity{

    @BindView(R.id.textResult)
    TextView textResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);



    }

    @OnClick(R.id.buttonScan)
    public void buttonStart(){
        if(MysqlLocalConnector.getInstance().isDatabaseConnected()) {
            Intent i = new Intent(this, NewDocumentActivity.class);
            startActivity(i);
        }else {
           Utils.createDialog(this, "Baza danych", "Baza danych nie jest jeszcze gotowa, poczekaj.");
        }
    }








}
