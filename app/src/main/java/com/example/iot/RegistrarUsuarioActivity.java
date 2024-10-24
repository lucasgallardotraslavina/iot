package com.example.iot;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrarUsuarioActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button buttonRegister;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.buttonRegister);

        db = FirebaseFirestore.getInstance();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> usuario = new HashMap<>();
        usuario.put("username", username);
        usuario.put("password", password);

        // Ahora manejamos la respuesta con un Task<DocumentReference>
        db.collection("usuarios").add(usuario)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegistrarUsuarioActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(RegistrarUsuarioActivity.this, "Error al registrar", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
