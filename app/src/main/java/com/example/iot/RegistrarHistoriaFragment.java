package com.example.iot;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrarHistoriaFragment extends Fragment {

    private EditText etHistoria;
    private Button btnGuardarHistoria;
    private LinearLayout historiasLayout;
    private FirebaseFirestore db;
    private List<Historia> historias;
    private String historiaIdParaEditar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registrar_historia, container, false);

        etHistoria = view.findViewById(R.id.et_historia);
        btnGuardarHistoria = view.findViewById(R.id.btn_guardar_historia);
        historiasLayout = view.findViewById(R.id.historias_layout);
        db = FirebaseFirestore.getInstance();
        historias = new ArrayList<>();

        btnGuardarHistoria.setOnClickListener(v -> guardarHistoria());
        obtenerHistorias();

        return view;
    }

    private void guardarHistoria() {
        String historiaTexto = etHistoria.getText().toString().trim();

        if (TextUtils.isEmpty(historiaTexto)) {
            etHistoria.setError("Por favor escribe una historia");
            return;
        }

        Map<String, String> historiaMap = new HashMap<>();
        historiaMap.put("texto", historiaTexto);

        if (historiaIdParaEditar != null) {
            db.collection("historias").document(historiaIdParaEditar)
                    .set(historiaMap)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Historia actualizada", Toast.LENGTH_SHORT).show();
                        etHistoria.setText("");
                        historiaIdParaEditar = null;
                        obtenerHistorias();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error al actualizar la historia", Toast.LENGTH_SHORT).show();
                    });
        } else {
            db.collection("historias")
                    .add(historiaMap)
                    .addOnSuccessListener(documentReference -> {
                        etHistoria.setText("");
                        obtenerHistorias();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error al guardar la historia", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void obtenerHistorias() {
        historiasLayout.removeAllViews();
        historias.clear();

        db.collection("historias")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Historia historia = document.toObject(Historia.class);
                            historia.setId(document.getId());
                            historias.add(historia);
                            agregarHistoriaATextView(historia);
                        }
                    } else {
                        Toast.makeText(getContext(), "Error al obtener historias", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void agregarHistoriaATextView(Historia historia) {
        TextView textView = new TextView(getContext());
        textView.setText(historia.getTexto());
        textView.setPadding(0, 10, 0, 10); // Añadir un poco de padding
        historiasLayout.addView(textView);
        Button btnEditar = new Button(getContext());
        btnEditar.setText("Editar");
        btnEditar.setOnClickListener(v -> editarHistoria(historia));
        Button btnEliminar = new Button(getContext());
        btnEliminar.setText("Eliminar");
        btnEliminar.setOnClickListener(v -> eliminarHistoria(historia.getId()));

        historiasLayout.addView(btnEditar);
        historiasLayout.addView(btnEliminar);
    }

    private void editarHistoria(Historia historia) {
        etHistoria.setText(historia.getTexto());
        historiaIdParaEditar = historia.getId();
    }

    private void eliminarHistoria(String historiaId) {
        db.collection("historias").document(historiaId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Historia eliminada", Toast.LENGTH_SHORT).show();
                    obtenerHistorias();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al eliminar la historia", Toast.LENGTH_SHORT).show();
                });
    }

    private static class Historia {
        private String id;
        private String texto;

        public Historia() {} // Necesario para Firestore

        public Historia(String texto) {
            this.texto = texto;
        }

        public String getTexto() {
            return texto;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }
}
