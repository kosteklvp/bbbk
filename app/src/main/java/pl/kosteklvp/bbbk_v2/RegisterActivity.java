package pl.kosteklvp.bbbk_v2;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

public class RegisterActivity extends AppCompatActivity {

    EditText etFirstName;
    EditText etLastName;
    EditText etEmail;
    EditText etPassword1;
    EditText etPassword2;
    CardView buttonRegister;

    String firstName, lastName, email, password1, password2;
    int statusCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFirstName = findViewById(R.id.firstNameField);
        etLastName = findViewById(R.id.lastNameField);
        etEmail = findViewById(R.id.emailField);
        etPassword1 = findViewById(R.id.passwordField);
        etPassword2 = findViewById(R.id.passwordField2);
        buttonRegister = findViewById(R.id.cardViewCreate);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstName = etFirstName.getText() + "";
                lastName = etLastName.getText() + "";
                email = etEmail.getText() + "";
                password1 = etPassword1.getText() + "";
                password2 = etPassword2.getText() + "";

                if(firstName.equals("") || lastName.equals("") || email.equals("") || password1.equals("") || password2.equals("")) {
                    Toast.makeText(getApplicationContext(), "Pola wypełnione niepoprawnie", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!email.contains("@")) {
                    Toast.makeText(getApplicationContext(), "Pole Email wypełnione niepoprawnie", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!password1.equals(password2)) {
                    Toast.makeText(getApplicationContext(), "Hasła są różne", Toast.LENGTH_SHORT).show();
                    return;
                }

                Networking n = new Networking();
                n.execute();


            }
        });
    }

    private void _(String s) {
        Log.d("RegisterActivity", s);
    }

    public class Networking extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            getJson("http://bbbk.2ap.pl/api/users");
            return null;
        }
    }

    private void getJson(String url) {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(url);
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();

        postParameters.add(new BasicNameValuePair("name", firstName + " " + lastName));
        postParameters.add(new BasicNameValuePair("email", email));
        postParameters.add(new BasicNameValuePair("password", password1));


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

        statusCode = Integer.parseInt(decodeResultIntoJson(stringBuffer + ""));

        if(statusCode==201) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "Rejestracja przebiegła pomyślnie", Toast.LENGTH_SHORT).show();
                }
            });
            finish();
        }
        else if(statusCode==500) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "Użytkownik o podanym adresie email już istnieje", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private String decodeResultIntoJson(String response) {

        String status_code = "0";
            try {
                JSONObject jo = new JSONObject(response);

                status_code = jo.getString("status_code");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return status_code;
    }
}
