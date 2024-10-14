package com.example.iot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

public class DatosMascotaFragment extends Fragment {

    private TextView nombreDueñoTextView;
    private TextView direccionTextView;
    private TextView telefonoTextView;
    private TextView nombreMascotaTextView;
    private TextView especieTextView;
    private TextView razaTextView;
    private TextView sexoTextView;
    private TextView colorTextView;
    private TextView fechaNacimientoTextView;
    private ImageView imagenMascota;

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_datos_mascota, container, false);

        db = FirebaseFirestore.getInstance();

        nombreDueñoTextView = view.findViewById(R.id.nombreDueñoTextView);
        direccionTextView = view.findViewById(R.id.direccionTextView);
        telefonoTextView = view.findViewById(R.id.telefonoTextView);
        nombreMascotaTextView = view.findViewById(R.id.nombreMascotaTextView);
        especieTextView = view.findViewById(R.id.especieTextView);
        razaTextView = view.findViewById(R.id.razaTextView);
        sexoTextView = view.findViewById(R.id.sexoTextView);
        colorTextView = view.findViewById(R.id.colorTextView);
        fechaNacimientoTextView = view.findViewById(R.id.fechaNacimientoTextView);
        imagenMascota = view.findViewById(R.id.imagenMascota);
        String mascotaId = getArguments() != null ? getArguments().getString("mascotaId") : null;

        if (mascotaId != null) {
            cargarDatosMascota(mascotaId);
        } else {
            Toast.makeText(getContext(), "No se encontró el ID de la mascota", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void cargarDatosMascota(String mascotaId) {
        db.collection("mascotas").document(mascotaId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        nombreDueñoTextView.setText(documentSnapshot.getString("nombre_dueño"));
                        direccionTextView.setText(documentSnapshot.getString("direccion"));
                        telefonoTextView.setText(documentSnapshot.getString("telefono"));
                        nombreMascotaTextView.setText(documentSnapshot.getString("nombre_mascota"));
                        especieTextView.setText(documentSnapshot.getString("especie"));
                        razaTextView.setText(documentSnapshot.getString("raza"));
                        sexoTextView.setText(documentSnapshot.getString("sexo"));
                        colorTextView.setText(documentSnapshot.getString("color"));
                        Object fechaNacimiento = documentSnapshot.get("fecha_nacimiento");
                        if (fechaNacimiento instanceof String) {
                            fechaNacimientoTextView.setText((String) fechaNacimiento);
                        } else {
                            fechaNacimientoTextView.setText("Fecha no disponible");
                        }

                        String imagenUrl = documentSnapshot.getString("imagen_mascota");
                        if (imagenUrl != null && !imagenUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(imagenUrl)
                                    .placeholder(R.drawable.iot_logo)
                                    .into(imagenMascota);
                        } else {
                            imagenMascota.setImageResource(R.drawable.iot_logo);
                        }
                    } else {
                        Toast.makeText(getActivity(), "No se encontró la mascota", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error al cargar los datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
