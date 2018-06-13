package pl.kosteklvp.bbbk_v2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BoardActivity extends AppCompatActivity {

    TextView tvBoard;
    CardView buttonBack;
    String user_id;
    String boardId;
    String boardTitle;
    ExpandableListView expListView;
    ExpandableListAdapter listAdapter;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    List<String> listOfListasId;
    ImageView buttonAdd;

    List<Lista> listOfListas;
    List<String> listOfListasNames;

    int onPressedItemId;
    int onNewTaskTableID;

    static boolean shouldRefresh = false;

    @Override
    public void onResume() {
        super.onResume();

        if(shouldRefresh) {
            listOfListasNames = new ArrayList<String>();
            listOfListasId = new ArrayList<String>();
            listOfListas = new ArrayList<Lista>();




            Networking n = new Networking();
            try {
                Object obj = n.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            NetworkingForTasks n2 = new NetworkingForTasks();
            try {
                Object obj = n2.execute().get();
                _("executed");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            if(listOfListas.size()>0) {

                prepareListData();

                listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
                expListView.setAdapter(listAdapter);


            }

        }

        shouldRefresh = false;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        buttonBack = findViewById(R.id.cardViewCreate);
        tvBoard = findViewById(R.id.tvBoard);
        expListView = findViewById(R.id.exListView);
        buttonAdd = findViewById(R.id.buttonAddList);


        Intent intent = getIntent();

        boardTitle = intent.getStringExtra("boardTitle");
        boardId = intent.getStringExtra("boardId");
        user_id = intent.getStringExtra("userId");

        listOfListasNames = new ArrayList<String>();
        listOfListasId = new ArrayList<String>();
        listOfListas = new ArrayList<Lista>();




        Networking n = new Networking();
        try {
            Object obj = n.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }



        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent conn_Board_CreateList = new Intent(BoardActivity.this, CreateListActivity.class);
                conn_Board_CreateList.putExtra("user_id", user_id);
                conn_Board_CreateList.putExtra("board_id", boardId);

                startActivity(conn_Board_CreateList);

            }
        });

        tvBoard.setText(boardTitle);

        NetworkingForTasks n2 = new NetworkingForTasks();
        try {
            Object obj = n2.execute().get();
            _("executed");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(listOfListas.size()>0) {

            prepareListData();

            listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
            expListView.setAdapter(listAdapter);


        }

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {


                if(listOfListas.get(i).listOfTasksTitles.get(i1).equals("Dodaj nowe zadanie")) {
                    _(listOfListas.get(i).getId() + "");

                }

                return false;
            }
        });


    }

    public class Networking extends AsyncTask {


        @Override
        protected Object doInBackground(Object[] objects) {

            getJson("http://bbbk.2ap.pl/api/boards/" + boardId +"/pages");
            _("http://bbbk.2ap.pl/api/boards/" + boardId +"/pages");

            return null;
        }
    }

    private void _(String s) {
        Log.d("MyApp", "BoardActivity" + "   " + s);
    }

    private void getJson(String url) {

        StringBuffer response = null;

        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            int responseCode = con.getResponseCode();

            _("Sending to url " + url);
            _("response code: " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            _(response.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        decodeResultIntoArray(response + "");

    }


    private void decodeResultIntoArray(String response) {

        try {
            JSONArray jsonArr = new JSONArray(response);

            listOfListas = new ArrayList<Lista>();

            for(int i=0; i<jsonArr.length(); i++) {
                JSONObject jsonObject = jsonArr.getJSONObject(i);
                Lista lista = new Lista();
                lista.setId(Integer.parseInt(jsonObject.getString("id")));
                lista.setTitle(jsonObject.getString("title"));
                lista.setDescription(jsonObject.getString("description"));
                lista.setUser_id(Integer.parseInt(jsonObject.getString("user_id")));
                lista.setBoard_id(Integer.parseInt(jsonObject.getString("board_id")));

                listOfListas.add(lista);
                listOfListasNames.add(jsonObject.getString("title"));
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        for(int i=0; i<listOfListas.size(); i++) {
            listDataHeader.add(listOfListas.get(i).getTitle());
            listOfListasId.add(String.valueOf(listOfListas.get(i).getId()));
        }

        for(int i=0; i<listDataHeader.size(); i++) {

            for(int j=0; j<listOfListas.get(i).listOfTasks.size(); j++) {
                listDataChild.put(listDataHeader.get(i), listOfListas.get(i).listOfTasksTitles);

            }

        }

    }

    public class NetworkingForTasks extends AsyncTask {


        @Override
        protected Object doInBackground(Object[] objects) {

            for(int i=0; i<listOfListas.size(); i++) {
                getJsonForTasks("http://bbbk.2ap.pl/api/pages/" + listOfListas.get(i).getId() +"/tasks", String.valueOf(i));
                _("wykonywanie jsonfortask " + i);
            }

            return null;
        }
    }

    private void getJsonForTasks(String url, String id) {

        StringBuffer response = null;

        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            int responseCode = con.getResponseCode();

            _("Sending to url " + url);
            _("response code [" + id + "]: " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print in String
            _(response.toString());
            _("3");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        decodeResultIntoTask(response + "", Integer.parseInt(id));
        _("lista: " + id + " ||| " +response);

    }

    private void decodeResultIntoTask(String response, int id) {

        try {
            JSONArray jsonArr = new JSONArray(response);

            listOfListas.get(id).listOfTasks = new ArrayList<Task>();
            listOfListas.get(id).listOfTasksTitles = new ArrayList<String>();

            for(int j=0; j<jsonArr.length(); j++) {
                JSONObject jsonObject = jsonArr.getJSONObject(j);
                Task task = new Task();
                task.setId(Integer.parseInt(jsonObject.getString("id")));
                task.setTitle(jsonObject.getString("title"));
                task.setDescription(jsonObject.getString("description"));
                task.setUser_id(Integer.parseInt(jsonObject.getString("user_id")));
                task.setBoard_id(Integer.parseInt(jsonObject.getString("board_id")));
                task.setFinished(Integer.parseInt(jsonObject.getString("finished")));
                task.setPage_id(Integer.parseInt(jsonObject.getString("page_id")));


                listOfListas.get(id).listOfTasks.add(task);
                listOfListas.get(id).listOfTasksTitles.add(jsonObject.getString("title"));

            }
            listOfListas.get(id).listOfTasksTitles.add("Dodaj nowe zadanie");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



}


