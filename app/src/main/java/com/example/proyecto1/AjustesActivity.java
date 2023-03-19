package com.example.proyecto1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.Locale;

public class AjustesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);
        // Inicializa los Spinners de color de fondo y color principal
        Spinner spinnerBackground = findViewById(R.id.spinnerBackground);
        Spinner spinnerMain = findViewById(R.id.spinnerMain);
        // Obtiene los recursos de la aplicación
        Resources res = this.getResources();

        String[] backgrounds = {res.getString(R.string.Azul), res.getString(R.string.Negro)};
        String[] mains = {res.getString(R.string.Naranja), res.getString(R.string.Verde)};

        ArrayAdapter<String> adapterBackground = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, backgrounds);
        ArrayAdapter<String> adapterMains = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, mains);
        spinnerBackground.setAdapter(adapterBackground);
        spinnerMain.setAdapter(adapterMains);

        RadioGroup idiomas = findViewById(R.id.radioGroupIdioma);
        RadioButton ingles = findViewById(R.id.Ingles);
        RadioButton español = findViewById(R.id.Español);

        if (res.getConfiguration().getLocales().get(0).getLanguage()=="en") {
            ingles.setChecked(true);
            español.setChecked(false);
        }else {
            ingles.setChecked(false);
            español.setChecked(true);
        }

        idiomas.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.Ingles:
                        setIdioma("en");
                        finish();
                        startActivity(getIntent());
                        break;
                    case R.id.Español:
                        setIdioma("es");
                        finish();
                        startActivity(getIntent());
                        break;
                }
            }
        });
    }

    private void setIdioma(String idioma){
        Locale locale = new Locale(idioma);
        locale.setDefault(locale);
        Resources res = this.getResources();
        Configuration conf = res.getConfiguration();
        conf.setLocale(locale);
        res.updateConfiguration(conf, res.getDisplayMetrics());
    }


}