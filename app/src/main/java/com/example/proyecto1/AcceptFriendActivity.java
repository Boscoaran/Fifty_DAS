package com.example.proyecto1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class AcceptFriendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_friend);

        Intent intent = getIntent();
        String user = intent.getStringExtra("user");

        GameServerDB bd = GameServerDB.getDB();

        try {
            bd.gestion_amigos(this, "obtener_amigos", "", user, new GameServerDB.OnResponseListener() {
                @Override
                public void onResponse(JSONObject response) throws JSONException {
                    Log.d("", "onResponse: "+response);
                }

                @Override
                public void onError(VolleyError error) {

                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
/*
        if (c.moveToFirst()) { //Si hay solicitudes pendientes
            ArrayList<String> friendsArrayList = new ArrayList<String>();
            friendsArrayList.add(c.getString(0));
            while (c.moveToNext()) {//Mientras haya más amigos en el cursor
                friendsArrayList.add(c.getString(0)); //Agrega el siguiente amigo al ArrayList
            }
            String[] friendsArray = Arrays.copyOf(friendsArrayList.toArray(), friendsArrayList.toArray().length, String[].class);
            ListView friends = (ListView) findViewById(R.id.list);
            AdaptadorListView adaptadorListView = new AdaptadorListView(getApplicationContext(), friendsArray);
            friends.setAdapter(adaptadorListView);//Asigna el adaptador a la ListView
        } else { //Si no hay solicitudes pendientes
        String[] friendsArray = {};
        ListView friends = (ListView) findViewById(R.id.list);
        AdaptadorListView adaptadorListView = new AdaptadorListView(getApplicationContext(), friendsArray);//cargar amigos en adapter
        friends.setAdapter(adaptadorListView);
        friends.setClickable(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(AcceptFriendActivity.this);
        friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {//pulsar un amigo
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String friendName = friendsArray[i];
                builder.setTitle(R.string.Aceptar);
                builder.setMessage(R.string.Preg_accept);
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {//no aceptar solicitud
                        bd.eliminarSolicitud(user, friendName);
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {//aceptar solicitud
                        bd.aceptarSolicitud(user, friendName);
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });

        }*/
    }
}