package com.example.happyplant.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happyplant.R;
import com.example.happyplant.model.Planta;

import java.util.ArrayList;
public class PlantasAdapter extends RecyclerView.Adapter<PlantasAdapter.PlantaViewHolder> {

    private ArrayList<Planta> listaPlantas;

    public PlantasAdapter(ArrayList<Planta> listaPlantas) {
        this.listaPlantas = listaPlantas;
    }

    @NonNull
    @Override
    public PlantaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_planta, parent, false);
        return new PlantaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantaViewHolder holder, int position) {
        Planta planta = listaPlantas.get(position);
        holder.txtNombre.setText(planta.getNombre());
        holder.txtId.setText(planta.getId());
    }

    @Override
    public int getItemCount() {
        return listaPlantas.size();
    }

    public static class PlantaViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtId;

        public PlantaViewHolder(@NonNull View itemView) {
            super(itemView);
            // Cambiado para coincidir con los ids del nuevo XML
            txtNombre = itemView.findViewById(R.id.tvNombrePlanta);
            txtId = itemView.findViewById(R.id.tvIdMaceta);
        }
    }
}
