package com.example.iot;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {

    private LinearLayout petImagesContainer;
    private ImageButton imageButtonMascota;

    private FirebaseFirestore db;
    private String mascotaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        petImagesContainer = findViewById(R.id.pet_images_container);
        imageButtonMascota = findViewById(R.id.imageButtonMascota);

        cargarImagenMascota();

        imageButtonMascota.setOnClickListener(view -> mostrarDatosMascota());
    }

    private void cargarImagenMascota() {
        db.collection("mascotas")
                .orderBy("fechaRegistro", Query.Direction.DESCENDING)
                .limit(1) //
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        mascotaId = documentSnapshot.getId();
                        mostrarDatosMascota(documentSnapshot);
                    } else {
                        imageButtonMascota.setImageResource(R.drawable.iot_logo);
                    }
                })
                .addOnFailureListener(e -> {
                    imageButtonMascota.setImageResource(R.drawable.iot_logo);
                });
    }

    private void mostrarDatosMascota(DocumentSnapshot documentSnapshot) {
        String imageUrl = documentSnapshot.getString("imagen_mascota");

        if (imageUrl != null && !imageUrl.isEmpty()) {
            StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
            Glide.with(MainActivity.this)
                    .load(imageUrl)
                    .placeholder(R.drawable.iot_logo)
                    .into(imageButtonMascota);
        } else {
            imageButtonMascota.setImageResource(R.drawable.iot_logo);
        }
        String nombreMascota = documentSnapshot.getString("nombre_mascota");
        Toast.makeText(MainActivity.this, "Nombre de la mascota: " + nombreMascota, Toast.LENGTH_SHORT).show();
    }

    private void mostrarDatosMascota() {
        if (mascotaId != null) {
            petImagesContainer.setVisibility(LinearLayout.GONE);
            DatosMascotaFragment datosMascotaFragment = new DatosMascotaFragment();
            Bundle args = new Bundle();
            args.putString("mascotaId", mascotaId);
            datosMascotaFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, datosMascotaFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            Toast.makeText(MainActivity.this, "No se encontró el ID de la mascota", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_registrar_mascota) {
            petImagesContainer.setVisibility(LinearLayout.GONE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RegistrarMascotaFragment())
                    .addToBackStack(null)
                    .commit();
            return true;
        } else if (id == R.id.action_registrar_historia) {
            petImagesContainer.setVisibility(LinearLayout.GONE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RegistrarHistoriaFragment())
                    .addToBackStack(null)
                    .commit();
            return true;
        } else if (id == R.id.action_ver_revisiones) {
            petImagesContainer.setVisibility(LinearLayout.GONE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ListaRevisionesFragment())
                    .addToBackStack(null)
                    .commit();
            return true;
        } else if (id == R.id.action_volver_inicio) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            petImagesContainer.setVisibility(LinearLayout.VISIBLE);
            cargarImagenMascota();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            petImagesContainer.setVisibility(LinearLayout.VISIBLE);
            cargarImagenMascota();
        }
        super.onBackPressed();
    }
}
