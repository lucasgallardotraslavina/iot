package com.example.iot;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class EditarMascotaFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText nombreDueñoEditText;
    private EditText direccionEditText;
    private EditText telefonoEditText;
    private EditText nombreMascotaEditText;
    private EditText especieEditText;
    private EditText razaEditText;
    private EditText sexoEditText;
    private EditText colorEditText;
    private EditText fechaNacimientoEditText;
    private ImageView imagenMascotaImageView;
    private Button btnElegirImagen;
    private Button btnGuardarCambios;

    private FirebaseFirestore db;
    private StorageReference storageReference;
    private String mascotaId;
    private Uri imagenUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_mascota, container, false);

        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("mascotas");


        nombreDueñoEditText = view.findViewById(R.id.nombreDueñoEditText);
        direccionEditText = view.findViewById(R.id.direccionEditText);
        telefonoEditText = view.findViewById(R.id.telefonoEditText);
        nombreMascotaEditText = view.findViewById(R.id.nombreMascotaEditText);
        especieEditText = view.findViewById(R.id.especieEditText);
        razaEditText = view.findViewById(R.id.razaEditText);
        sexoEditText = view.findViewById(R.id.sexoEditText);
        colorEditText = view.findViewById(R.id.colorEditText);
        fechaNacimientoEditText = view.findViewById(R.id.fechaNacimientoEditText);
        imagenMascotaImageView = view.findViewById(R.id.imagenMascotaImageView);
        btnElegirImagen = view.findViewById(R.id.btnElegirImagen);
        btnGuardarCambios = view.findViewById(R.id.btnGuardarCambios);

        mascotaId = getArguments() != null ? getArguments().getString("mascotaId") : null;

        if (mascotaId != null) {
            cargarDatosMascota(mascotaId);
        }

        btnElegirImagen.setOnClickListener(v -> abrirGaleria());

        btnGuardarCambios.setOnClickListener(v -> {
            if (imagenUri != null) {
                subirImagenYGuardarMascota();
            } else {
                guardarMascota(null);
            }
        });

        return view;
    }

    private void cargarDatosMascota(String mascotaId) {
        db.collection("mascotas").document(mascotaId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        nombreDueñoEditText.setText(documentSnapshot.getString("nombre_dueño"));
                        direccionEditText.setText(documentSnapshot.getString("direccion"));
                        telefonoEditText.setText(documentSnapshot.getString("telefono"));
                        nombreMascotaEditText.setText(documentSnapshot.getString("nombre_mascota"));
                        especieEditText.setText(documentSnapshot.getString("especie"));
                        razaEditText.setText(documentSnapshot.getString("raza"));
                        sexoEditText.setText(documentSnapshot.getString("sexo"));
                        colorEditText.setText(documentSnapshot.getString("color"));
                        fechaNacimientoEditText.setText(documentSnapshot.getString("fecha_nacimiento"));

                        String imagenUrl = documentSnapshot.getString("imagen_mascota");
                        Glide.with(this)
                                .load(imagenUrl)
                                .into(imagenMascotaImageView);
                    }
                })
                .addOnFailureListener(e -> {
                });
    }

    private void abrirGaleria() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar Imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imagenUri = data.getData();
            imagenMascotaImageView.setImageURI(imagenUri);
        }
    }

    private void subirImagenYGuardarMascota() {
        StorageReference fileReference = storageReference.child(mascotaId + ".jpg");

        fileReference.putFile(imagenUri)
                .addOnSuccessListener(taskSnapshot -> {
                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        guardarMascota(uri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                });
    }

    private void guardarMascota(String imagenUrl) {
        Map<String, Object> mascotaData = new HashMap<>();
        mascotaData.put("nombre_dueño", nombreDueñoEditText.getText().toString());
        mascotaData.put("direccion", direccionEditText.getText().toString());
        mascotaData.put("telefono", telefonoEditText.getText().toString());
        mascotaData.put("nombre_mascota", nombreMascotaEditText.getText().toString());
        mascotaData.put("especie", especieEditText.getText().toString());
        mascotaData.put("raza", razaEditText.getText().toString());
        mascotaData.put("sexo", sexoEditText.getText().toString());
        mascotaData.put("color", colorEditText.getText().toString());
        mascotaData.put("fecha_nacimiento", fechaNacimientoEditText.getText().toString());

        if (imagenUrl != null) {
            mascotaData.put("imagen_mascota", imagenUrl);
        }

        db.collection("mascotas").document(mascotaId)
                .update(mascotaData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Mascota actualizada", Toast.LENGTH_SHORT).show();
                    getFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error al actualizar la mascota", Toast.LENGTH_SHORT).show();
                });
    }
}
