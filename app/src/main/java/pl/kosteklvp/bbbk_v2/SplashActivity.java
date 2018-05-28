package pl.kosteklvp.bbbk_v2;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread splash = new Thread() {
          public void run() {
              try {
                  sleep(2000);

                  if(isNetworkAvailable()) {
                      Toast.makeText(getApplicationContext(), "Brak połączenia z internetem. Włącz internet i spróbuj ponownie", Toast.LENGTH_LONG).show();
                  }

                  sleep(1500);
              }
              catch (Exception e) {
                  e.printStackTrace();
              }
              finally {
                  Intent conn_Splash_Login = new Intent(SplashActivity.this, LoginActivity.class);
                  startActivity(conn_Splash_Login);
              }
          }
        };

           splash.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
