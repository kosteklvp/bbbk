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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.HttpDelete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    TextView tvLogged;
    CardView buttonLogout;
    String loggedInAs;
    ListView listView;
    ImageView buttonAdd;
    List<Board> listOfBoards;
    List<String> listOfBoardsNames;
    List<String> listOfBoardsDescriptions;

    int onPressedItemId;
    String onPressedItemTitle;

    static boolean shouldRefresh = false;



    @Override
    public void onResume() {
        super.onResume();

        if(shouldRefresh) {
            _("refreshing");
            listOfBoardsNames = new ArrayList<String>();
            listOfBoardsDescriptions = new ArrayList<String>();

            Networking n = new Networking();
            try {
                Object obj = n.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


                for (int i = 0; i<listOfBoards.size(); i++) {
                    listOfBoardsNames.add(listOfBoards.get(i).getTitle());
                }

                ArrayAdapter arrayAdapterOfBoards = new ArrayAdapter(MainActivity.this, R.layout.board_item, R.id.title, listOfBoardsNames);

                listView.setAdapter(arrayAdapterOfBoards);
                registerForContextMenu(listView);




        }

        shouldRefresh = false;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLogged = findViewById(R.id.tvBoard);
        buttonLogout = findViewById(R.id.cardViewCreate);
        listView = findViewById(R.id.listView);
        buttonAdd = findViewById(R.id.buttonAdd);


        Intent intent = getIntent();

        String email = intent.getStringExtra("email");
        String id = intent.getStringExtra("id");
        loggedInAs = id;
        String name = intent.getStringExtra("name");

        listOfBoardsNames = new ArrayList<String>();
        listOfBoardsDescriptions = new ArrayList<String>();

        tvLogged.setText(name);

        Networking n = new Networking();
        try {
            Object obj = n.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent conn_Main_CreateBoard = new Intent(MainActivity.this, CreateBoardActivity.class);
                conn_Main_CreateBoard.putExtra("user_id", loggedInAs);

                startActivity(conn_Main_CreateBoard);
            }
        });



        tvLogged.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginActivity.loggedInAs = -1;

                Intent conn_Main_Login = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(conn_Main_Login);
                finish();

            }
        });

        if(listOfBoards.size()>0) {

            for (int i = 0; i<listOfBoards.size(); i++) {
                listOfBoardsNames.add(listOfBoards.get(i).getTitle());
            }

            ArrayAdapter arrayAdapterOfBoards = new ArrayAdapter(MainActivity.this, R.layout.board_item, R.id.title, listOfBoardsNames);

            listView.setAdapter(arrayAdapterOfBoards);
            registerForContextMenu(listView);



        } else {
            _("lista tablic pusta");


            ArrayAdapter arrayAdapterOfBoards = new ArrayAdapter(MainActivity.this, R.layout.board_item, R.id.title, listOfBoardsNames);

            listView.setAdapter(arrayAdapterOfBoards);
            registerForContextMenu(listView);
        }

        /*listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {


                return true;
            }
        });*/

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                Intent conn_Main_Board = new Intent(MainActivity.this, BoardActivity.class);
                conn_Main_Board.putExtra("boardTitle", String.valueOf(listOfBoards.get((int) adapterView.getItemIdAtPosition(i)).getTitle()));
                _(String.valueOf(listOfBoards.get((int) adapterView.getItemIdAtPosition(i)).getTitle()));

                conn_Main_Board.putExtra("boardId", String.valueOf(listOfBoards.get((int) adapterView.getItemIdAtPosition(i)).getId()));
                _(String.valueOf(listOfBoards.get((int) adapterView.getItemIdAtPosition(i)).getId()));

                conn_Main_Board.putExtra("userId", String.valueOf(listOfBoards.get((int) adapterView.getItemIdAtPosition(i)).getUser_id()));
                _(String.valueOf(listOfBoards.get((int) adapterView.getItemIdAtPosition(i)).getUser_id()));
                startActivity(conn_Main_Board);

            }
        });

    }

    public class Networking extends AsyncTask {


        @Override
        protected Object doInBackground(Object[] objects) {

            getJson("http://bbbk.2ap.pl/api/users/" + loggedInAs + "/boards");
            _("http://bbbk.2ap.pl/api/users/" + loggedInAs + "/boards");
            return null;
        }
    }

    private void _(String s) {
        Log.d("MyApp", "MainActivity" + "   " + s);
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

            //print in String
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

            listOfBoards = new ArrayList<Board>();

            for(int i=0; i<jsonArr.length(); i++) {
                JSONObject jsonObject = jsonArr.getJSONObject(i);
                Board board = new Board();
                board.setId(Integer.parseInt(jsonObject.getString("id")));
                board.setTitle(jsonObject.getString("title"));
                board.setDescription(jsonObject.getString("description"));
                board.setUser_id(Integer.parseInt(jsonObject.getString("user_id")));

                listOfBoards.add(board);

            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public class NetworkingForDelete extends AsyncTask {

        int board_id;

        public NetworkingForDelete(int board_id) {
            this.board_id = board_id;
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            getJsonForDelete("http://bbbk.2ap.pl/api/boards/" + board_id);

            return null;
        }
    }

    private void getJsonForDelete(String url) {

        StringBuffer response = null;

        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            con.setRequestMethod("DELETE");
            _("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO" + con.getResponseCode());



        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Adapter adapter = listView.getAdapter();
        Object item = adapter.getItem(info.position);



        onPressedItemId = listOfBoards.get((int) adapter.getItemId(info.position)).getId();
        onPressedItemTitle = listOfBoards.get((int) adapter.getItemId(info.position)).getTitle();
        menu.setHeaderTitle(listOfBoards.get((int) adapter.getItemId(info.position)).getTitle());
        menu.add(0, v.getId(), 0, "Modyfikuj");
        menu.add(0, v.getId(), 0, "Usuń");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        if(item.getTitle()=="Modyfikuj"){

        }
        else if(item.getTitle()=="Usuń"){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Czy na pewno chcesz usunąć tablicę " + onPressedItemTitle + "?").setPositiveButton("Tak", dialogClickListener)
                    .setNegativeButton("Nie", dialogClickListener).show();
        } else {
            return false;
        }
        return true;
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    NetworkingForDelete n3 = new NetworkingForDelete(onPressedItemId);
                    try {
                        Object obj = n3.execute().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    listOfBoardsNames = new ArrayList<String>();
                    listOfBoardsDescriptions = new ArrayList<String>();

                    Networking n = new Networking();
                    try {
                        Object obj = n.execute().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }


                    for (int i = 0; i<listOfBoards.size(); i++) {
                        listOfBoardsNames.add(listOfBoards.get(i).getTitle());
                    }

                    ArrayAdapter arrayAdapterOfBoards = new ArrayAdapter(MainActivity.this, R.layout.board_item, R.id.title, listOfBoardsNames);

                    listView.setAdapter(arrayAdapterOfBoards);
                    registerForContextMenu(listView);



                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

}
