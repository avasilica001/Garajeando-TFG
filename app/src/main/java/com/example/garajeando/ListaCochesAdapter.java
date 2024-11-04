package com.example.garajeando;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.bumptech.glide.Glide;

public class ListaCochesAdapter extends RecyclerView.Adapter<ListaCochesAdapter.CocheHolder> {

    private final Activity context;

    private final Activity activity;

    //arraylist para cada columna en la bd
    private ArrayList<Coche> coches=new ArrayList<Coche>();

    CardView l_coches;

    String usuario, idComunidad;
    Integer numCochesOtrasComunidades;

    String[] opciones;



    public ListaCochesAdapter(Activity activity, Activity context, ArrayList<Coche> coches, String usuario, String idComunidad, Integer numCochesOtrasComunidades, String[] opciones) {
        this.context = context;
        this.activity = activity;
        this.coches=coches;
        this.usuario=usuario;
        this.idComunidad=idComunidad;
        this.numCochesOtrasComunidades=numCochesOtrasComunidades;
        this.opciones=opciones;
    }

    @NonNull
    @Override
    public CocheHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View l = LayoutInflater.from(context).inflate(R.layout.layout_coches, parent, false);

        return new CocheHolder(l);
    }

    @Override
    public void onBindViewHolder(@NonNull CocheHolder holder, @SuppressLint("RecyclerView") int p) {

        if ((getItemCount() - 1) == p){
            holder.matriculaTextView.setVisibility(View.GONE);
            holder.imagenCocheImageView.setVisibility(View.GONE);
            holder.anadirCocheButton.setVisibility(View.VISIBLE);
        } else {
            holder.matriculaTextView.setVisibility(View.VISIBLE);
            holder.imagenCocheImageView.setVisibility(View.VISIBLE);
            holder.anadirCocheButton.setVisibility(View.GONE);

            holder.matriculaTextView.setText(String.valueOf(coches.get(p).matricula));
            Glide.with(this.context).load("http://ec2-51-20-10-72.eu-north-1.compute.amazonaws.com/imagenes/fotoscoches/"+ coches.get(p).nombreFotoPrincipal).into(holder.imagenCocheImageView);

            holder.id = coches.get(p).getIdCoche();
            holder.propietario = coches.get(p).getPropietario();
            holder.nombrePropietario = coches.get(p).getNombrePropietario();
            holder.apellidosPropietario = coches.get(p).getApellidosPropietario();
            holder.matricula = coches.get(p).getMatricula();
            holder.marca = coches.get(p).getMarca();
            holder.modelo = coches.get(p).modelo;
            holder.plazas = coches.get(p).plazas;
            holder.puertas = coches.get(p).puertas;
            holder.transmision = coches.get(p).getTransmision();
            holder.combustible = coches.get(p).combustible;
            holder.aireacondicionado = coches.get(p).aireAcondicionado;
            holder.bluetooth = coches.get(p).getBluetooth();
            holder.gps = coches.get(p).getGps();
            holder.descripcion = coches.get(p).getDescripcion();
        }

        holder.card_coche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((getItemCount() - 1) == p){
                    ((ComunidadElegida) activity).dialogoAnadirCoche();
                }else{
                    Intent intent = new Intent(context, CocheElegido.class);

                    intent.putExtra("usuario", usuario);
                    intent.putExtra("idComunidad", idComunidad);
                    intent.putExtra("idCoche", coches.get(p).getIdCoche());
                    intent.putExtra("propietario", coches.get(p).getPropietario());
                    intent.putExtra("nombrePropietario", coches.get(p).getNombrePropietario());
                    intent.putExtra("apellidosPropietario", coches.get(p).getApellidosPropietario());
                    intent.putExtra("matricula", coches.get(p).getMatricula());
                    intent.putExtra("marca", coches.get(p).getMarca());
                    intent.putExtra("modelo", coches.get(p).getModelo());
                    intent.putExtra("plazas", coches.get(p).getPlazas());
                    intent.putExtra("puertas", coches.get(p).getPuertas());
                    intent.putExtra("transmision", coches.get(p).getTransmision());
                    intent.putExtra("combustible", coches.get(p).getCombustible());
                    intent.putExtra("aireacondicionado", coches.get(p).getAireAcondicionado());
                    intent.putExtra("bluetooth", coches.get(p).getBluetooth());
                    intent.putExtra("gps", coches.get(p).getGps());
                    intent.putExtra("descripcion", coches.get(p).descripcion);

                    activity.startActivityForResult(intent, 2);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        // +1 por la card de añadir coche
        return coches.size() + 1;
    }

    class CocheHolder extends RecyclerView.ViewHolder{

        TextView matriculaTextView;
        ImageView imagenCocheImageView;
        Button anadirCocheButton;
        CardView card_coche;

        String id, propietario, nombrePropietario, apellidosPropietario, matricula, marca, modelo, transmision, combustible, descripcion;
        Integer plazas, puertas;
        Boolean aireacondicionado, bluetooth, gps;

        public CocheHolder(@NonNull View itemView) {
            super(itemView);
            matriculaTextView = itemView.findViewById(R.id.matriculaCocheTextView);
            imagenCocheImageView = itemView.findViewById(R.id.imagenPreviaCocheImageView);
            anadirCocheButton = itemView.findViewById(R.id.anadirCocheButton);
            card_coche = itemView.findViewById(R.id.card_reserva);
        }
    }
}