package pl.kosteklvp.bbbk_v2;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {


    CardView buttonLogin;
    TextView buttonRegister;
    EditText etUsername;
    EditText etPassword;
    String username;
    String password;

    static int loggedInAs = -1;
    int statusCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        buttonLogin = findViewById(R.id.cardViewCreate);
        buttonRegister = findViewById(R.id.register);
        etPassword = findViewById(R.id.passwordField);
        etUsername = findViewById(R.id.loginField);

        int statusCode;

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                username = etUsername.getText() + "";
                password = etPassword.getText() + "";

                Networking n = new Networking();
                n.execute();

            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent conn_Login_Register = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(conn_Login_Register);
                etPassword.setText("");
                etUsername.setText("");
            }
        });
    }

    private void _(String s) {
        Log.d("MyApp", "LoginActivity" + "   " + s);
    }

    public class Networking extends AsyncTask {


        @Override
        protected Object doInBackground(Object[] objects) {

            getJson("http://bbbk.2ap.pl/api/users/login");
            return null;
        }
    }

    private void getJson(String url) {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(url);
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();


        postParameters.add(new BasicNameValuePair("email", username));
        postParameters.add(new BasicNameValuePair("password", password));


        BufferedReader bufferedReader = null;
        StringBuffer stringBuffer = new StringBuffer("");
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParameters);
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);

            bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            String line = "";
            String LineSeparator = System.getProperty("line.separator");
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + LineSeparator);
            }
            bufferedReader.close();
        } catch (Exception e) {
            _("Error doing networking | " + e.getMessage());
            e.printStackTrace();
        }
        _("result: " + stringBuffer);

        decodeResultIntoJson(stringBuffer + "");

        if(statusCode==400) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "Podane dane są nieprawidłowe", Toast.LENGTH_SHORT).show();
                    statusCode = -1;
                }
            });
        }

    }

    private void decodeResultIntoJson(String response) {

        /*{
        "id":152,
        "name":"Jan Jan",
        "email":"jan@wp.pl",
        "created_at":"2018-05-14 10:42:56",
        "updated_at":"2018-05-14 10:42:56"
        }*/



        try {
            JSONObject jo = new JSONObject(response);

            if(response.contains("status_code")) {
                statusCode = Integer.parseInt(jo.getString("status_code"));
            }
            else {
                String name = jo.getString("name");
                String email = jo.getString("email");
                String id = jo.getString("id");


                loggedInAs = Integer.parseInt(id);

                Intent conn_Login_Main = new Intent(LoginActivity.this, MainActivity.class);
                conn_Login_Main.putExtra("name", name);
                conn_Login_Main.putExtra("email", email);
                conn_Login_Main.putExtra("id", id);
                startActivity(conn_Login_Main);
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
