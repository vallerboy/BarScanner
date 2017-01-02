package pl.oskarpolak.barscanner2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {


    @BindView(R.id.editLogin)
    EditText editLogin;

    @BindView(R.id.editPassword)
    EditText editPassword;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }


    @OnClick(R.id.buttonLogin)
    public void onLogin() {
        if(editLogin.getText().toString().equals("firma") && editPassword.getText().toString().equals("firma123")) {
             startActivity(new Intent(this, MainActivity.class));
             finish();
        }
    }
}
