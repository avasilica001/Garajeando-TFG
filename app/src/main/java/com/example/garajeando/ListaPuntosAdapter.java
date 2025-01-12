package com.example.garajeando;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ListaPuntosAdapter extends RecyclerView.Adapter<ListaPuntosAdapter.PuntosHolder> {

    private final Activity context;

    private final Activity activity;

    //arraylist para cada columna en la bd
    private ArrayList<Puntos> puntos=new ArrayList<Puntos>();

    String usuario, idComunidad, tipoReservas;

    public ListaPuntosAdapter(Activity activity, Activity context, ArrayList<Puntos> puntos, String usuario) {
        this.context = context;
        this.activity = activity;
        this.puntos = puntos;
        this.usuario = usuario;
    }

    @NonNull
    @Override
    public ListaPuntosAdapter.PuntosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View l = LayoutInflater.from(context).inflate(R.layout.layout_puntos, parent, false);

        return new ListaPuntosAdapter.PuntosHolder(l);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaPuntosAdapter.PuntosHolder holder, @SuppressLint("RecyclerView") int p) {
        holder.puntosHistorialTextView.setText(String.valueOf(puntos.get(p).getPuntos()));
        holder.fechaHoraHistorialTextView.setText(String.valueOf(puntos.get(p).getFechaHora()));
        holder.descripcionHistorialTextView.setText(String.valueOf(puntos.get(p).getDecripcion()));
    }

    @Override
    public int getItemCount() {
        return puntos.size();
    }

    class PuntosHolder extends RecyclerView.ViewHolder{

        TextView puntosHistorialTextView, fechaHoraHistorialTextView, descripcionHistorialTextView;
        CardView card_puntos;

        String usuario, puntos, fechaHora, descripcion;

        public PuntosHolder(@NonNull View itemView) {
            super(itemView);
            puntosHistorialTextView = itemView.findViewById(R.id.puntosHistorialTextView);
            fechaHoraHistorialTextView = itemView.findViewById(R.id.fechaHoraHistorialTextView);
            descripcionHistorialTextView = itemView.findViewById(R.id.descripcionHistorialTextView);
        }
    }
}
