package com.example.proyecto1;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AjustesActivity extends AppCompatActivity {

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    ImageView imgPerfil;
    Button btnImg;
    String currentPhotoPath;
    StorageReference storageReference;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        btnImg = findViewById(R.id.buttonImg);
        storageReference = FirebaseStorage.getInstance().getReference();

        ImageView imgPerfil = findViewById(R.id.imagePerfil);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)!=
                    PackageManager.PERMISSION_GRANTED) {
                //PEDIR EL PERMISO
                ActivityCompat.requestPermissions(this, new
                        String[]{Manifest.permission.POST_NOTIFICATIONS}, 11);
            }
        }
        btnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermissions();
            }
        });
        // Inicializa los Spinners de color de fondo y color principal
        Spinner spinnerBackground = findViewById(R.id.spinnerBackground);
        Spinner spinnerMain = findViewById(R.id.spinnerMain);
        // Obtiene los recursos de la aplicación
        Resources res = this.getResources();

        String[] backgrounds = {res.getString(R.string.Azul), res.getString(R.string.Negro)};
        String[] mains = {res.getString(R.string.Naranja), res.getString(R.string.Verde)};
        //Setear los coloes de los adapters spinners
        ArrayAdapter<String> adapterBackground = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, backgrounds);
        ArrayAdapter<String> adapterMains = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, mains);
        //Setear spinners con adapters
        spinnerBackground.setAdapter(adapterBackground);
        spinnerMain.setAdapter(adapterMains);

        RadioGroup idiomas = findViewById(R.id.radioGroupIdioma);
        RadioButton ingles = findViewById(R.id.Ingles);
        RadioButton español = findViewById(R.id.Español);
        //marcar boton de idioma actual
        if (res.getConfiguration().getLocales().get(0).getLanguage()=="en") {
            ingles.setChecked(true);
            español.setChecked(false);
        }else {
            ingles.setChecked(false);
            español.setChecked(true);
        }

        idiomas.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {//si se cambia el idioma
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.Ingles:
                        setIdioma("en");//cambiar idioma a ingles
                        finish();
                        startActivity(getIntent());//recargar
                        break;
                    case R.id.Español:
                        setIdioma("es");//cambiar idioma a español
                        finish();
                        startActivity(getIntent());//recargar
                        break;
                }
            }
        });
    }

    private void askCameraPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                imgPerfil.setImageURI(Uri.fromFile(f));
                Log.d("tag", "ABsolute Url of Image is " + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
                uploadImageToFirebase(f.getName(), contentUri);
            }
        }

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "." + getFileExt(contentUri);
                Log.d("tag", "onActivityResult: Gallery Image Uri:  " + imageFileName);
                imgPerfil.setImageURI(contentUri);
                uploadImageToFirebase(imageFileName, contentUri);
            }
        }
    }

    private void uploadImageToFirebase(String name, Uri contentUri) {
        final StorageReference image = storageReference.child("pictures/" + name);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("tag", "onSuccess: Uploaded Image URl is " + uri.toString());
                        FirebaseMessaging.getInstance().getToken()
                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                    @Override
                                    public void onComplete(@NonNull Task<String> task) {
                                        if (!task.isSuccessful()) {
                                            Log.w("fcm", "Fetching FCM registration token failed", task.getException());
                                            return;
                                        }

                                        // Conseguir el token
                                        token = task.getResult();

                                        // Log and toast
                                        Log.d("fcm", "El token del dispositivo: " + token);
                                        // Despues de obtener el token se manda un POST a un PHP con el token para que el PHP se lo mande a Firebase y finalmente se mande una notificacion al dispositivo
                                        // para agradecer al usuario que haya fotografiado el logo de la universidad

                                        // Se realiza la identificacion
                                        Data data = new Data.Builder()
                                                .putString("token", token).build();
                                        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(FirebaseNotificacion.class)
                                                .setInputData(data).build();
                                        WorkManager.getInstance(AjustesActivity.this).enqueue(otwr);
                                        WorkManager.getInstance(AjustesActivity.this).getWorkInfoByIdLiveData(otwr.getId()).observe(AjustesActivity.this, new Observer<WorkInfo>() {
                                            @Override
                                            public void onChanged(@Nullable WorkInfo workInfo) {
                                                if (workInfo != null && workInfo.getState().isFinished()) {
                                                    String resultado = workInfo.getOutputData().getString("result");
                                                    // Si el php devuelve que se ha identificado CORRECTAMENTE
                                                    Log.d("fcm", "notificacionFirebase.php devuelve "+resultado);

                                                }
                                            }
                                        });
                                    }
                                });
                    }
                });
                Toast.makeText(AjustesActivity.this, "Firebase upload SUCCEED", Toast.LENGTH_SHORT).show();
            }
            // El onFailureListener se activa cuando falla
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AjustesActivity.this, "Firebase upload failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent

        Log.d("Titos", "entraaqui?");

        // Create the File where the photo should go
        File photoFile = null;
        Log.d("Titos", "entraaqui2?");
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {

        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Log.d("Titos", "entra?");
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.example.android.Proyecto1_Das.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
        }

    }

    private void setIdioma(String idioma){//metodo para cambiar el idioma
        Locale locale = new Locale(idioma);
        locale.setDefault(locale);
        Resources res = this.getResources();
        Configuration conf = res.getConfiguration();
        conf.setLocale(locale);
        res.updateConfiguration(conf, res.getDisplayMetrics());//cambio en la configuracion
        //revisar para eliminar el deprecated
    }


}