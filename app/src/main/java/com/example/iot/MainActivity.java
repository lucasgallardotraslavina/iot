package com.example.iot;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configurar el toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Cargar el MainFragment al iniciar la aplicación
        if (savedInstanceState == null) {
            loadFragment(new MainFragment());
        }
    }

    // Método para cargar el fragmento
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }


    // Inflar el menú
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Manejar los clics en el menú
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_registrar_mascota) {
            loadFragment(new RegistrarMascotaFragment());
            return true;
        } else if (id == R.id.action_registrar_historia) {
            loadFragment(new RegistrarHistoriaFragment());
            return true;
        } else if (id == R.id.action_ver_revisiones) {
            loadFragment(new ListaRevisionesFragment());
            return true;
        } else if (id == R.id.action_volver_inicio) {
            loadFragment(new MainFragment());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
