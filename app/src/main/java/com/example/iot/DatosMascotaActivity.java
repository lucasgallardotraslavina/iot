package com.example.iot;

import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class DatosMascotaActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private String documentId;

    private TextView txtNombre, txtEspecie, txtRaza, txtColor;
    private ImageView imgMascota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_mascota);

        documentId = getIntent().getStringExtra("documentId");

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        txtNombre = findViewById(R.id.txtNombreMascota);
        txtEspecie = findViewById(R.id.txtEspecieMascota);
        txtRaza = findViewById(R.id.txtRazaMascota);
        txtColor = findViewById(R.id.txtColorMascota);
        imgMascota = findViewById(R.id.imgMascota);

        Button btnActualizar = findViewById(R.id.btnActualizar);
        Button btnEliminar = findViewById(R.id.btnEliminar);

        cargarDatosMascota(documentId);

        btnActualizar.setOnClickListener(v -> mostrarDialogoActualizar());
        btnEliminar.setOnClickListener(v -> eliminarMascota());
    }

    private void cargarDatosMascota(String documentId) {
        db.collection("mascotas").document(documentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombre = documentSnapshot.getString("nombre");
                        String especie = documentSnapshot.getString("especie");
                        String raza = documentSnapshot.getString("raza");
                        String color = documentSnapshot.getString("color");
                        String imageUrl = documentSnapshot.getString("imageUrl");

                        txtNombre.setText(nombre);
                        txtEspecie.setText(especie);
                        txtRaza.setText(raza);
                        txtColor.setText(color);

                        Picasso.get().load(imageUrl).into(imgMascota);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DatosMascotaActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
                });
    }

    private void mostrarDialogoActualizar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Actualizar datos de la mascota");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText inputNombre = new EditText(this);
        final EditText inputEspecie = new EditText(this);
        final EditText inputRaza = new EditText(this);
        final EditText inputColor = new EditText(this);

        inputNombre.setHint("Nombre");
        inputEspecie.setHint("Especie");
        inputRaza.setHint("Raza");
        inputColor.setHint("Color");


        inputNombre.setInputType(InputType.TYPE_CLASS_TEXT);
        inputEspecie.setInputType(InputType.TYPE_CLASS_TEXT);
        inputRaza.setInputType(InputType.TYPE_CLASS_TEXT);
        inputColor.setInputType(InputType.TYPE_CLASS_TEXT);

        inputNombre.setText(txtNombre.getText().toString());
        inputEspecie.setText(txtEspecie.getText().toString());
        inputRaza.setText(txtRaza.getText().toString());
        inputColor.setText(txtColor.getText().toString());

        layout.addView(inputNombre);
        layout.addView(inputEspecie);
        layout.addView(inputRaza);
        layout.addView(inputColor);

        builder.setView(layout);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nombre = inputNombre.getText().toString();
            String especie = inputEspecie.getText().toString();
            String raza = inputRaza.getText().toString();
            String color = inputColor.getText().toString();

            actualizarDatosMascota(nombre, especie, raza, color);
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }


    private void actualizarDatosMascota(String nombre, String especie, String raza, String color) {
        db.collection("mascotas").document(documentId)
                .update("nombre", nombre, "especie", especie, "raza", raza, "color", color)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(DatosMascotaActivity.this, "Datos actualizados", Toast.LENGTH_SHORT).show();
                    cargarDatosMascota(documentId);  // Recargar los datos
                })
                .addOnFailureListener(e -> Toast.makeText(DatosMascotaActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show());
    }

    private void eliminarMascota() {
        db.collection("mascotas").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(DatosMascotaActivity.this, "Mascota eliminada", Toast.LENGTH_SHORT).show();
                    finish();  // Cerrar la actividad
                })
                .addOnFailureListener(e -> Toast.makeText(DatosMascotaActivity.this, "Error al eliminar", Toast.LENGTH_SHORT).show());
    }
}
