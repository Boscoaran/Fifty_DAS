package com.example.proyecto1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class SearchFriendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);

        Intent intent = getIntent();
        String user = intent.getStringExtra("user");

        GameBD bd = GameBD.getmDB(this);//conexion BD

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
                String userName = editTextFriend.getText().toString();
                Cursor c = bd.searchUser(userName);//buscar usuario por nombre
                if (c.moveToFirst()){//usuario encontrado
                    String friend = c.getString(0);
                    builder.setTitle(R.string.AñadirAmigo);
                    builder.setMessage(R.string.PregAñadirAmigo);
                    builder.setPositiveButton(R.string.AñadirAmigo, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            bd.solicitarAmistad(user,friend);//enviar solicitud de amistad al usuario
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
        });



    }
}