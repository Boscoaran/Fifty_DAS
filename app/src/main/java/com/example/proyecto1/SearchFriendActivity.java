package com.example.proyecto1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SearchFriendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);

        Intent intent = getIntent();
        String user = intent.getStringExtra("user");

        GameServerDB bd = GameServerDB.getDB();//conexion BD

        Context ctx = this;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton(R.string.Volver, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        ImageButton btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {//Este boton no se esta mostrando
            @Override
            public void onClick(View view) {
                EditText editTextFriend = findViewById(R.id.editTextFriend);
                String amigo = editTextFriend.getText().toString();

                try {
                    bd.gestion_amigos(ctx, "buscar_usuario", amigo, user, new GameServerDB.OnResponseListener() {
                        @Override
                        public void onResponse(JSONObject response) throws JSONException {
                            if ("usuario_encontrado".equals(response.getString("resultado"))) {
                                String nombreAmigo = response.getString("nombre");
                                String usuarioAmigo = response.getString("usuario");
                                builder.setTitle(R.string.AñadirAmigo);
                                builder.setMessage(R.string.PregAñadirAmigo);
                                builder.setPositiveButton(R.string.AñadirAmigo, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        try {
                                            bd.gestion_amigos(ctx, "hacer_amigos", usuarioAmigo, user, new GameServerDB.OnResponseListener() {
                                                @Override
                                                public void onResponse(JSONObject response) throws JSONException {
                                                    if ("solicitud_enviada".equals(response.getString("resultado"))){
                                                        builder.setTitle(R.string.Amigos);
                                                        builder.setMessage(R.string.solic_env);
                                                        builder.setPositiveButton(null,null);
                                                        builder.show();
                                                    } else if ("es_amigo".equals(response.getString("resultado"))){
                                                        builder.setTitle(R.string.Amigos);
                                                        builder.setMessage(R.string.Son_amigos);
                                                        builder.setPositiveButton(null,null);
                                                        builder.show();
                                                    } else if ("solicitud_ya_enviada".equals(response.getString("resultado"))){
                                                        builder.setTitle(R.string.Amigos);
                                                        builder.setMessage(R.string.solic_ya_enviada);
                                                        builder.setPositiveButton(null,null);
                                                        builder.show();
                                                    } else if ("esperando_respuesta".equals(response.getString("resultado"))){
                                                        builder.setTitle(R.string.Amigos);
                                                        builder.setMessage(R.string.solicitud_pendiente);
                                                        builder.setPositiveButton(null,null);
                                                        builder.show();
                                                }
                                                }
                                                @Override
                                                public void onError(VolleyError error) {

                                                }
                                            });
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                });
                                builder.show();
                            } else {
                                builder.setTitle("ERROR");//no existe el usuario buscado
                                builder.setMessage(R.string.erro_no_existe_usuario);
                                builder.setPositiveButton("", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                                builder.show();
                            }
                        }
                        @Override
                        public void onError(VolleyError error) {

                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}