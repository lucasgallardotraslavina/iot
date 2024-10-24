package com.example.iot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private Button btnModificarMascota;
    private Button btnEliminarMascota;
    private ImageView imagenMascota;

    private FirebaseFirestore db;
    private String mascotaId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_datos_mascota, container, false);

        db = FirebaseFirestore.getInstance();

        // Inicializar las vistas
        nombreDueñoTextView = view.findViewById(R.id.nombreDueñoTextView);
        direccionTextView = view.findViewById(R.id.direccionTextView);
        telefonoTextView = view.findViewById(R.id.telefonoTextView);
        nombreMascotaTextView = view.findViewById(R.id.nombreMascotaTextView);
        especieTextView = view.findViewById(R.id.especieTextView);
        razaTextView = view.findViewById(R.id.razaTextView);
        sexoTextView = view.findViewById(R.id.sexoTextView);
        colorTextView = view.findViewById(R.id.colorTextView);
        fechaNacimientoTextView = view.findViewById(R.id.fechaNacimientoTextView);
        imagenMascota = view.findViewById(R.id.imagenMascotaImageView);
        btnModificarMascota = view.findViewById(R.id.btnModificarMascota);
        btnEliminarMascota = view.findViewById(R.id.btnEliminarMascota);


        mascotaId = getArguments() != null ? getArguments().getString("mascotaId") : null;

        if (mascotaId != null) {
            cargarDatosMascota(mascotaId);
        }

        btnModificarMascota.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("mascotaId", mascotaId);

            EditarMascotaFragment editarMascotaFragment = new EditarMascotaFragment();
            editarMascotaFragment.setArguments(args);

            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, editarMascotaFragment)
                    .addToBackStack(null)
                    .commit();
        });


        btnEliminarMascota.setOnClickListener(v -> {
            eliminarMascota(mascotaId);
        });

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
                        fechaNacimientoTextView.setText(documentSnapshot.getString("fecha_nacimiento"));
                        String imagenUrl = documentSnapshot.getString("imagen_mascota");
                        Glide.with(this)
                                .load(imagenUrl)
                                .into(imagenMascota);
                    }
                })
                .addOnFailureListener(e -> {

                });
    }

    private void eliminarMascota(String mascotaId) {
        db.collection("mascotas").document(mascotaId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Mascota eliminada", Toast.LENGTH_SHORT).show();
                    getFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error al eliminar la mascota", Toast.LENGTH_SHORT).show();
                });
    }
}
