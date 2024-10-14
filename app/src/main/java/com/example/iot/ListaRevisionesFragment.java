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

import java.util.HashMap;
import java.util.Map;

public class ListaRevisionesFragment extends Fragment {

    private EditText etFecha, etPeso, etDesparasitario, etVacuna, etProxVacuna;
    private Button btnGuardar;
    private LinearLayout revisionesLayout;
    private FirebaseFirestore db;
    private String revisionIdParaEditar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_revisiones, container, false);
        etFecha = view.findViewById(R.id.et_fecha);
        etPeso = view.findViewById(R.id.et_peso);
        etDesparasitario = view.findViewById(R.id.et_desparasitario);
        etVacuna = view.findViewById(R.id.et_vacuna);
        etProxVacuna = view.findViewById(R.id.et_prox_vacuna);
        btnGuardar = view.findViewById(R.id.btn_guardar);
        revisionesLayout = view.findViewById(R.id.revisiones_layout);
        db = FirebaseFirestore.getInstance();
        btnGuardar.setOnClickListener(v -> guardarRevision());
        obtenerRevisiones();
        return view;
    }

    private void guardarRevision() {
        String fecha = etFecha.getText().toString().trim();
        String peso = etPeso.getText().toString().trim();
        String desparasitario = etDesparasitario.getText().toString().trim();
        String vacuna = etVacuna.getText().toString().trim();
        String proxVacuna = etProxVacuna.getText().toString().trim();

        if (TextUtils.isEmpty(fecha) || TextUtils.isEmpty(peso) ||
                TextUtils.isEmpty(desparasitario) || TextUtils.isEmpty(vacuna) ||
                TextUtils.isEmpty(proxVacuna)) {
            Toast.makeText(getContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, Object> revision = new HashMap<>();
        revision.put("fecha", fecha);
        revision.put("peso", peso);
        revision.put("desparasitario", desparasitario);
        revision.put("vacuna", vacuna);
        revision.put("proxVacuna", proxVacuna);

        if (revisionIdParaEditar != null) {
            db.collection("revisiones").document(revisionIdParaEditar)
                    .set(revision)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Revisión actualizada", Toast.LENGTH_SHORT).show();
                        clearFields();
                        revisionIdParaEditar = null;
                        obtenerRevisiones();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error al actualizar la revisión", Toast.LENGTH_SHORT).show();
                    });
        } else {
            db.collection("revisiones")
                    .add(revision)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Revisión guardada", Toast.LENGTH_SHORT).show();
                        clearFields();
                        agregarRevisionATextView(revision, documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error al guardar la revisión", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void clearFields() {
        etFecha.setText("");
        etPeso.setText("");
        etDesparasitario.setText("");
        etVacuna.setText("");
        etProxVacuna.setText("");
    }

    private void obtenerRevisiones() {
        revisionesLayout.removeAllViews();

        db.collection("revisiones")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> revision = document.getData();
                            agregarRevisionATextView(revision, document.getId());
                        }
                    } else {
                        Toast.makeText(getContext(), "Error al obtener revisiones", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void agregarRevisionATextView(Map<String, Object> revision, String documentId) {
        LinearLayout revisionLayout = new LinearLayout(getContext());
        revisionLayout.setOrientation(LinearLayout.VERTICAL);
        revisionLayout.setPadding(0, 10, 0, 10);

        TextView textView = new TextView(getContext());
        String revisionTexto = "Fecha: " + revision.get("fecha") +
                "\nPeso: " + revision.get("peso") +
                "\nDesparasitario: " + revision.get("desparasitario") +
                "\nVacuna: " + revision.get("vacuna") +
                "\nPróxima Vacuna: " + revision.get("proxVacuna");

        textView.setText(revisionTexto);

        Button btnEditar = new Button(getContext());
        btnEditar.setText("Editar");
        btnEditar.setOnClickListener(v -> editarRevision(documentId, revision));

        Button btnEliminar = new Button(getContext());
        btnEliminar.setText("Eliminar");
        btnEliminar.setOnClickListener(v -> eliminarRevision(documentId));
        revisionLayout.addView(textView);
        revisionLayout.addView(btnEditar);
        revisionLayout.addView(btnEliminar);

        revisionesLayout.addView(revisionLayout);
    }

    private void editarRevision(String documentId, Map<String, Object> revision) {
        etFecha.setText((String) revision.get("fecha"));
        etPeso.setText((String) revision.get("peso"));
        etDesparasitario.setText((String) revision.get("desparasitario"));
        etVacuna.setText((String) revision.get("vacuna"));
        etProxVacuna.setText((String) revision.get("proxVacuna"));

        revisionIdParaEditar = documentId;
        btnGuardar.setText("Actualizar");
    }

    private void eliminarRevision(String documentId) {
        db.collection("revisiones").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Revisión eliminada", Toast.LENGTH_SHORT).show();
                    obtenerRevisiones();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al eliminar la revisión", Toast.LENGTH_SHORT).show();
                });
    }
}
