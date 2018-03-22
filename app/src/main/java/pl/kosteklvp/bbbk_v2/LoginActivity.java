package pl.kosteklvp.bbbk_v2;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent conn_Login_Register = new Intent(LoginActivity.this, RegisterActivity.class);

        CardView buttonLogin = findViewById(R.id.cardViewLogin);
        TextView buttonRegister = findViewById(R.id.register);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sprawdzanie logowania
                Toast.makeText(getApplicationContext(), "Logowanie", Toast.LENGTH_SHORT).show();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent conn_Login_Register = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(conn_Login_Register);
            }
        });
    }
}
