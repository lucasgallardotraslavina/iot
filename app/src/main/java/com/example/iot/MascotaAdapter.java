package com.example.iot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MascotaAdapter extends RecyclerView.Adapter<MascotaAdapter.MascotaViewHolder> {

    private List<Mascota> listaMascotas;
    private OnMascotaClickListener onMascotaClickListener;

    // Interfaz para el listener de clic
    public interface OnMascotaClickListener {
        void onMascotaClick(Mascota mascota);
    }

    // Constructor que recibe la lista de mascotas y el listener
    public MascotaAdapter(List<Mascota> listaMascotas, OnMascotaClickListener onMascotaClickListener) {
        this.listaMascotas = listaMascotas;
        this.onMascotaClickListener = onMascotaClickListener;
    }

    @NonNull
    @Override
    public MascotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mascota, parent, false);
        return new MascotaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MascotaViewHolder holder, int position) {
        Mascota mascota = listaMascotas.get(position);

        // Mostrar nombre de la mascota
        holder.nombreTextView.setText(mascota.getNombre_mascota());

        // Cargar la imagen usando Glide
        Glide.with(holder.itemView.getContext())
                .load(mascota.getImagen_mascota())  // URL de la imagen de la mascota
                .into(holder.imagenImageView);

        // Configurar el listener para el clic
        holder.itemView.setOnClickListener(v -> onMascotaClickListener.onMascotaClick(mascota));
    }

    @Override
    public int getItemCount() {
        return listaMascotas.size();
    }

    // Clase ViewHolder para cada mascota
    static class MascotaViewHolder extends RecyclerView.ViewHolder {
        ImageView imagenImageView;
        TextView nombreTextView;

        public MascotaViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenImageView = itemView.findViewById(R.id.mascota_image_view);
            nombreTextView = itemView.findViewById(R.id.mascota_nombre_text_view);
        }
    }
}
