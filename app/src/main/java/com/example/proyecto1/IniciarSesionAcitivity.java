package com.example.proyecto1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class IniciarSesionAcitivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_iniciar_sesion);

        final GameBD db = GameBD.getmDB(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ERROR");
        builder.setPositiveButton(R.string.Aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        Button btnIniciarSesion = findViewById(R.id.btnIniciarSesion2);
        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameEditText = findViewById(R.id.editTextTextPersonName);
                String name = nameEditText.getText().toString();
                EditText passwordEditText = findViewById(R.id.editTextTextPassword);
                String password = passwordEditText.getText().toString();
                String result = db.usuarioCorrecto(name, password);
                if ("CORRECT".equals(result)){
                    Intent intent = new Intent(IniciarSesionAcitivity.this, MainMenuActivity.class);
                    intent.putExtra("user", name);
                    Log.d(name, "onClick: ");
                    startActivity(intent);
                } else if ("INCORRECT".equals(result)) {
                    builder.setMessage(R.string.error_contraseña_incorrecta);
                    builder.show();
                } else {
                    builder.setMessage(R.string.erro_no_existe_usuario);
                    builder.show();
                }
            }
        });
    }
}