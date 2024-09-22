package com.example.iot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class RegistrarMascota extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imgMascota;
    private Bitmap imageBitmap;
    private StorageReference mStorage;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_mascota);

        imgMascota = findViewById(R.id.imgMascotaRegistro);
        mStorage = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
    }

    public void tomarFoto(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imgMascota.setImageBitmap(imageBitmap);
        }
    }

    public void registrarMascota(View v) {
        EditText nombreMascota = findViewById(R.id.txtNombreMascota);
        EditText especieMascota = findViewById(R.id.txtEspecieMascota);
        EditText razaMascota = findViewById(R.id.txtRazaMascota);
        EditText colorMascota = findViewById(R.id.txtColorMascota);

        String nombre = nombreMascota.getText().toString();
        String especie = especieMascota.getText().toString();
        String raza = razaMascota.getText().toString();
        String color = colorMascota.getText().toString();

        if (imageBitmap != null && !nombre.isEmpty() && !especie.isEmpty() && !raza.isEmpty() && !color.isEmpty()) {
            StorageReference fotoRef = mStorage.child("images/" + nombre + ".jpg");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = fotoRef.putBytes(data);
            uploadTask.addOnSuccessListener(taskSnapshot -> fotoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();

                Map<String, Object> mascota = new HashMap<>();
                mascota.put("nombre", nombre);
                mascota.put("especie", especie);
                mascota.put("raza", raza);
                mascota.put("color", color);
                mascota.put("imageUrl", imageUrl);

                db.collection("mascotas").add(mascota).addOnSuccessListener(documentReference -> {
                    Toast.makeText(RegistrarMascota.this, "Mascota registrada", Toast.LENGTH_SHORT).show();
                    finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(RegistrarMascota.this, "Error al registrar mascota", Toast.LENGTH_SHORT).show();
                });
            }));
        } else {
            Toast.makeText(this, "Por favor, completa todos los campos y toma una foto", Toast.LENGTH_SHORT).show();
        }
    }
}
