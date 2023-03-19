package com.example.proyecto1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegistrarseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        final GameBD db = GameBD.getmDB(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        Button btnRegistrarse = findViewById(R.id.btnRegistrarse2);
        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameEditText = findViewById(R.id.editTextTextPersonName);
                String name = nameEditText.getText().toString();
                EditText passwordEditText = findViewById(R.id.editTextTextPassword);
                String password = passwordEditText.getText().toString();
                String existeUsuario = db.usuarioCorrecto(name, password);
                if (!existeUsuario.equals("NO USER")) {
                    builder.setTitle("ERROR");
                    builder.setMessage(R.string.error_usuario_existe);
                    builder.setPositiveButton(R.string.Aceptar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.show();
                } else {
                    db.crearUsuario(name, password);
                    Intent intent = new Intent(RegistrarseActivity.this, MainMenuActivity.class);
                    intent.putExtra("user", name);
                    Log.d(name, "onClick: ");
                    startActivity(intent);
                }
            }
        });
    }
}