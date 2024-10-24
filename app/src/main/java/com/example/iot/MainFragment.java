package com.example.iot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class MainFragment extends Fragment {

    private RecyclerView recyclerView;
    private MascotaAdapter mascotaAdapter;
    private ArrayList<Mascota> listaMascotas = new ArrayList<>();
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);


        recyclerView = view.findViewById(R.id.recyclerViewMascotas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        db = FirebaseFirestore.getInstance();


        cargarMascotas();

        return view;
    }


    private void cargarMascotas() {
        db.collection("mascotas").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listaMascotas.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Mascota mascota = document.toObject(Mascota.class);
                    listaMascotas.add(mascota);
                }

                mascotaAdapter = new MascotaAdapter(listaMascotas, mascota -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("mascotaId", mascota.getMascota_id());

                    DatosMascotaFragment datosMascotaFragment = new DatosMascotaFragment();
                    datosMascotaFragment.setArguments(bundle);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, datosMascotaFragment)
                            .addToBackStack(null)
                            .commit();
                });
                recyclerView.setAdapter(mascotaAdapter);
            }
        });
    }
}
