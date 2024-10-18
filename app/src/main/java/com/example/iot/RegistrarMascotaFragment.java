package com.example.iot;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class RegistrarMascotaFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageViewMascota;
    private EditText editTextNombreDueño, editTextDireccion, editTextTelefono, editTextNombreMascota, editTextEspecie, editTextRaza, editTextSexo, editTextColor, editTextFechaNacimiento;
    private Uri imageUri;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private String mascotaId; // Variable para almacenar el ID de la mascota si estamos editando

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registrar_mascota, container, false);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Inicializar vistas
        imageViewMascota = view.findViewById(R.id.imageViewMascota);
        editTextNombreDueño = view.findViewById(R.id.editTextNombreDueño);
        editTextDireccion = view.findViewById(R.id.editTextDireccion);
        editTextTelefono = view.findViewById(R.id.editTextTelefono);
        editTextNombreMascota = view.findViewById(R.id.editTextNombreMascota);
        editTextEspecie = view.findViewById(R.id.editTextEspecie);
        editTextRaza = view.findViewById(R.id.editTextRaza);
        editTextSexo = view.findViewById(R.id.editTextSexo);
        editTextColor = view.findViewById(R.id.editTextColor);
        editTextFechaNacimiento = view.findViewById(R.id.editTextFechaNacimiento);

        Button buttonSeleccionarImagen = view.findViewById(R.id.buttonSeleccionarImagen);
        Button buttonRegistrarMascota = view.findViewById(R.id.buttonRegistrarMascota);

        // Obtener el ID de la mascota si estamos editando
        if (getArguments() != null) {
            mascotaId = getArguments().getString("mascota_id");
            cargarDatosMascota(mascotaId);
        }

        buttonSeleccionarImagen.setOnClickListener(v -> openFileChooser());
        buttonRegistrarMascota.setOnClickListener(v -> registrarMascota());

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(imageViewMascota);
        }
    }

    private void registrarMascota() {
        String nombreDueño = editTextNombreDueño.getText().toString().trim();
        String direccion = editTextDireccion.getText().toString().trim();
        String telefono = editTextTelefono.getText().toString().trim();
        String nombreMascota = editTextNombreMascota.getText().toString().trim();
        String especie = editTextEspecie.getText().toString().trim();
        String raza = editTextRaza.getText().toString().trim();
        String sexo = editTextSexo.getText().toString().trim();
        String color = editTextColor.getText().toString().trim();
        String fechaNacimiento = editTextFechaNacimiento.getText().toString().trim();

        if (imageUri != null) {
            if (mascotaId == null) {
                // Registro de nueva mascota
                StorageReference fileReference = storage.getReference("mascotas/" + System.currentTimeMillis() + ".jpg");
                subirImagen(fileReference, nombreDueño, direccion, telefono, nombreMascota, especie, raza, sexo, color, fechaNacimiento);
            } else {
                // Edición de mascota existente
                StorageReference fileReference = storage.getReference("mascotas/" + mascotaId + ".jpg");
                subirImagen(fileReference, nombreDueño, direccion, telefono, nombreMascota, especie, raza, sexo, color, fechaNacimiento);
            }
        } else {
            Toast.makeText(getActivity(), "Por favor selecciona una imagen.", Toast.LENGTH_SHORT).show();
        }
    }

    private void subirImagen(StorageReference fileReference, String nombreDueño, String direccion, String telefono, String nombreMascota, String especie, String raza, String sexo, String color, String fechaNacimiento) {
        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        guardarDatosMascota(nombreDueño, direccion, telefono, nombreMascota, especie, raza, sexo, color, fechaNacimiento, imageUrl);
                    });
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error al subir la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void guardarDatosMascota(String nombreDueño, String direccion, String telefono, String nombreMascota, String especie, String raza, String sexo, String color, String fechaNacimiento, String imageUrl) {
        Map<String, Object> mascota = new HashMap<>();
        mascota.put("nombre_dueño", nombreDueño);
        mascota.put("direccion", direccion);
        mascota.put("telefono", telefono);
        mascota.put("nombre_mascota", nombreMascota);
        mascota.put("especie", especie);
        mascota.put("raza", raza);
        mascota.put("sexo", sexo);
        mascota.put("color", color);
        mascota.put("fecha_nacimiento", fechaNacimiento);
        mascota.put("imagen_mascota", imageUrl);
        mascota.put("fechaRegistro", FieldValue.serverTimestamp());

        if (mascotaId == null) {
            // Registro de nueva mascota
            db.collection("mascotas")
                    .add(mascota)
                    .addOnSuccessListener(documentReference -> {
                        documentReference.update("mascota_id", documentReference.getId())
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getActivity(), "Mascota registrada exitosamente!", Toast.LENGTH_SHORT).show();
                                    navigateToMainActivity();
                                })
                                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error al actualizar el ID de la mascota: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    })
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error al registrar la mascota: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            // Actualización de mascota existente
            db.collection("mascotas").document(mascotaId)
                    .set(mascota)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getActivity(), "Mascota actualizada exitosamente!", Toast.LENGTH_SHORT).show();
                        navigateToMainActivity();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error al actualizar la mascota: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void cargarDatosMascota(String mascotaId) {
        db.collection("mascotas").document(mascotaId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        editTextNombreDueño.setText(documentSnapshot.getString("nombre_dueño"));
                        editTextDireccion.setText(documentSnapshot.getString("direccion"));
                        editTextTelefono.setText(documentSnapshot.getString("telefono"));
                        editTextNombreMascota.setText(documentSnapshot.getString("nombre_mascota"));
                        editTextEspecie.setText(documentSnapshot.getString("especie"));
                        editTextRaza.setText(documentSnapshot.getString("raza"));
                        editTextSexo.setText(documentSnapshot.getString("sexo"));
                        editTextColor.setText(documentSnapshot.getString("color"));
                        editTextFechaNacimiento.setText(documentSnapshot.getString("fecha_nacimiento"));
                        String imageUrl = documentSnapshot.getString("imagen_mascota");
                        if (imageUrl != null) {
                            Glide.with(this).load(imageUrl).into(imageViewMascota);
                        }
                    } else {
                        Toast.makeText(getActivity(), "No se encontraron datos de la mascota.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error al cargar los datos de la mascota: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void navigateToMainActivity() {
        // Navega de vuelta a MainActivity
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new MainFragment())
                .commit();
    }
}
