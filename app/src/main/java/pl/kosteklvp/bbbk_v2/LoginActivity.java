package pl.kosteklvp.bbbk_v2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().hide();  //usuwanie titlebara
        setContentView(R.layout.activity_login);
    }
}
