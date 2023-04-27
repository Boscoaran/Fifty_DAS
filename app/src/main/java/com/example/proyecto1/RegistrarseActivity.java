package com.example.proyecto1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RegistrarseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        final GameServerDB db = GameServerDB.getDB();//conexion BD
        AlertDialog.Builder builder = new AlertDialog.Builder(this);//crear dialogo

        Button btnRegistrarse = findViewById(R.id.btnRegistrarse2);

        Context ctx = this;
        btnRegistrarse.setOnClickListener(new View.OnClickListener() {//registro
            @Override
            public void onClick(View v) {
                EditText usernameEditText = findViewById(R.id.editUserName);
                String username = usernameEditText.getText().toString();
                EditText passwordEditText = findViewById(R.id.editPassword);
                String password = passwordEditText.getText().toString();
                EditText nameEditTex = findViewById(R.id.editName);
                String name = nameEditTex.getText().toString();
                try {
                    db.gestion_usuarios(ctx, "crear_cuenta", username, name, password, new GameServerDB.OnResponseListener() {
                        @Override
                        public void onResponse(JSONObject response) throws JSONException {
                            if ("username_repetido".equals(response.getString("resultado"))) {//ya existe usuario con ese nombre
                                builder.setTitle("ERROR");
                                builder.setMessage(R.string.error_usuario_existe);
                                builder.setPositiveButton(R.string.Aceptar, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });
                                builder.show();
                            } else if ("usuario_creado".equals(response.getString("resultado"))){//usuario creado correctamente
                                Intent intent = new Intent(RegistrarseActivity.this, MainMenuActivity.class);
                                intent.putExtra("user", username);
                                startActivity(intent);
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