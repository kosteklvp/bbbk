package pl.kosteklvp.bbbk_v2;

import android.content.Intent;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CreateBoardActivity extends AppCompatActivity {

    CardView buttonBack;
    CardView buttonCreate;
    String loggedInAs;
    EditText eTitle;
    EditText eDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_board);

        Intent intent = getIntent();

        buttonBack = findViewById(R.id.cardViewBack);
        buttonCreate = findViewById(R.id.cardViewCreate);
        loggedInAs = intent.getStringExtra("user_id");
        eTitle = findViewById(R.id.editTextTitle);
        eDescription = findViewById(R.id.editTextDescription);

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Networking n = new Networking();
                try {
                    Object obj = n.execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                MainActivity.shouldRefresh = true;
                finish();
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
    }

    public class Networking extends AsyncTask {


        @Override
        protected Object doInBackground(Object[] objects) {

            getJson("http://bbbk.2ap.pl/api/boards");
            return null;
        }
    }

    private void _(String s) {
        Log.d("MyApp", "CreateBoardActivity" + "   " + s);
    }


    private void getJson(String url) {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost request = new HttpPost(url);
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();


        postParameters.add(new BasicNameValuePair("title", eTitle.getText() + ""));
        postParameters.add(new BasicNameValuePair("description", eDescription.getText() + ""));
        postParameters.add(new BasicNameValuePair("id", loggedInAs));


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




    }


}
