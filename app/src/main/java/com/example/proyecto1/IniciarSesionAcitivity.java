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

public class IniciarSesionAcitivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_iniciar_sesion);

        final GameServerDB db = GameServerDB.getDB();//conexion BD

        AlertDialog.Builder builder = new AlertDialog.Builder(this);//alert dialog creado
        builder.setTitle("ERROR");
        builder.setPositiveButton(R.string.Aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        Button btnIniciarSesion = findViewById(R.id.btnIniciarSesion2);

        Context ctx = this;
        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("", "BOTON PULSADO");
                EditText nameEditText = findViewById(R.id.editUserName);
                String username = nameEditText.getText().toString();
                EditText passwordEditText = findViewById(R.id.editPassword);
                String password = passwordEditText.getText().toString();
                try {
                    db.gestion_usuarios(ctx,"iniciar_sesion", username, "name", password, new GameServerDB.OnResponseListener() {
                        @Override
                        public void onResponse(JSONObject response) throws JSONException {
                            if ("registro_correcto".equals(response.getString("resultado"))) {//el usuario se logea correctamente
                                Intent intent = new Intent(IniciarSesionAcitivity.this, MainMenuActivity.class);
                                intent.putExtra("user", username);
                                startActivity(intent);
                            } else if ("contrasena_incorrecta".equals(response.getString("resultado"))) {//contraseña incorrecta
                                builder.setMessage(R.string.error_contraseña_incorrecta);
                                builder.show();
                            } else if ("usuario_no_encontrado".equals(response.getString("resultado"))) {//no existe usuario con ese nombre
                                builder.setMessage(R.string.erro_no_existe_usuario);
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