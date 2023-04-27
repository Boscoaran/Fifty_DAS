package com.example.proyecto1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FriendsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        Intent intent = getIntent();
        String user = intent.getStringExtra("user");

        GameServerDB bd = GameServerDB.getDB();

        try {
            bd.gestion_amigos(this, "obtener_amigos", "", user, new GameServerDB.OnResponseListener() {
                @Override
                public void onResponse(JSONObject response) throws JSONException {
                    List<JSONObject> objetosJSON = new ArrayList<>();

                    JSONArray jsonArray = new JSONArray(response);

                    Log.d("", "onResponse: "+jsonArray.length());
                    if (jsonArray.length()>0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Log.d("", "DATOOOOOOOOS: "+jsonObject);
                            objetosJSON.add(jsonObject);
                        }

                        for (JSONObject objetoJSON : objetosJSON) {
                            ArrayList<String> friendsArrayList = new ArrayList<String>();
                            friendsArrayList.add(objetoJSON.getString("amigo"));
                            String[] friendsArray = Arrays.copyOf(friendsArrayList.toArray(), friendsArrayList.toArray().length, String[].class);
                            ListView friends = (ListView) findViewById(R.id.list);
                            AdaptadorListView adaptadorListView = new AdaptadorListView(getApplicationContext(), friendsArray);
                            friends.setAdapter(adaptadorListView);
                        }
                    } else {//si no tiene amigos
                        String[] friendsArray = {};
                        ListView friends = (ListView) findViewById(R.id.list);
                        AdaptadorListView adaptadorListView = new AdaptadorListView(getApplicationContext(), friendsArray);
                        friends.setAdapter(adaptadorListView);
                    }
                    ImageButton btnAddFriend = findViewById(R.id.addFriend);
                    btnAddFriend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {//pantala para añadir amigos
                            Intent intent = new Intent(FriendsActivity.this, SearchFriendActivity.class);
                            intent.putExtra("user", user);
                            startActivity(intent);
                        }
                    });
                    ImageButton btnAcceptFriend = findViewById(R.id.acceptFriend);
                    btnAcceptFriend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {//pantalla para aceptar amigos
                            Intent intent = new Intent(FriendsActivity.this, AcceptFriendActivity.class);
                            intent.putExtra("user", user);
                            startActivity(intent);
                        }
                    });
                }

                @Override
                public void onError(VolleyError error) {

                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

