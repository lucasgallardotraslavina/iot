package com.example.iot;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class Principal extends AppCompatActivity {

    private ImageView imgMascota;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private String documentId;  // ID del documento de la mascota registrada

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        imgMascota = findViewById(R.id.imgMascota);
        Button btnRegistrarMascota = findViewById(R.id.btnRegistrarMascota);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        btnRegistrarMascota.setOnClickListener(v -> {
            Intent intent = new Intent(Principal.this, RegistrarMascota.class);
            startActivity(intent);
        });

        cargarImagenMascota();

        imgMascota.setOnClickListener(v -> {
            if (documentId != null) {
                Intent intent = new Intent(Principal.this, DatosMascotaActivity.class);
                intent.putExtra("documentId", documentId);
                startActivity(intent);
            } else {
                Toast.makeText(Principal.this, "No hay mascota registrada", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarImagenMascota() {
        db.collection("mascotas").limit(1).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        documentId = document.getId();
                        String imageUrl = document.getString("imageUrl");

                        // Cargar la imagen usando Picasso
                        StorageReference imageRef = storage.getReferenceFromUrl(imageUrl);
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            Picasso.get().load(uri).into(imgMascota);
                        }).addOnFailureListener(e -> {
                            Toast.makeText(Principal.this, "Error al cargar imagen", Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Principal.this, "Error al recuperar datos", Toast.LENGTH_SHORT).show();
                });
    }
}
