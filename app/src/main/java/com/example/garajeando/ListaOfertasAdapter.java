package com.example.garajeando;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;

import java.util.ArrayList;

public class ListaOfertasAdapter extends RecyclerView.Adapter<ListaOfertasAdapter.OfertaHolder> {

    private final Activity context;

    private final Activity activity;

    //arraylist para cada columna en la bd
    private ArrayList<Oferta> ofertas=new ArrayList<Oferta>();

    private JSONArray respuestaInfoCoches;

    String usuario, idComunidad, tiempo;

    public ListaOfertasAdapter(Activity activity, Activity context, ArrayList<Oferta> ofertas, String usuario, String idComunidad, String tiempo) {
        this.context = context;
        this.activity = activity;
        this.ofertas=ofertas;
        this.usuario=usuario;
        this.idComunidad=idComunidad;
        this.tiempo=tiempo;
    }

    @NonNull
    @Override
    public ListaOfertasAdapter.OfertaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View l = LayoutInflater.from(context).inflate(R.layout.layout_ofertas, parent, false);

        return new ListaOfertasAdapter.OfertaHolder(l);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaOfertasAdapter.OfertaHolder holder, @SuppressLint("RecyclerView") int p) {

        if ((getItemCount() - 1) == p && tiempo.equals("futuras")){
            holder.imagenPreviaCocheOfertaImageView.setVisibility(View.GONE);
            holder.matriculaCocheOfertaTextView.setVisibility(View.GONE);
            holder.inicioOfertaTextView.setVisibility(View.GONE);
            holder.finalOfertaTextView.setVisibility(View.GONE);
            holder.anadirOfertaButton.setVisibility(View.VISIBLE);
        } else {
            holder.anadirOfertaButton.setVisibility(View.GONE);
            holder.imagenPreviaCocheOfertaImageView.setVisibility(View.VISIBLE);
            holder.matriculaCocheOfertaTextView.setVisibility(View.VISIBLE);
            holder.inicioOfertaTextView.setVisibility(View.VISIBLE);
            holder.finalOfertaTextView.setVisibility(View.VISIBLE);

            holder.matriculaCocheOfertaTextView.setText(String.valueOf(ofertas.get(p).matricula));
            holder.inicioOfertaTextView.setText(String.valueOf("Inicio: " + ofertas.get(p).fechaHoraInicio));
            holder.finalOfertaTextView.setText(String.valueOf("Fin: " + ofertas.get(p).fechaHoraFin));
            Glide.with(this.context).load("http://ec2-51-20-10-72.eu-north-1.compute.amazonaws.com/imagenes/fotoscoches/"+ ofertas.get(p).fotoCoche).into(holder.imagenPreviaCocheOfertaImageView);

            holder.idOferta = ofertas.get(p).getIdOferta();
            holder.idCoche = ofertas.get(p).getIdCoche();
            holder.idComunidad = ofertas.get(p).getIdComunidad();
            holder.fechaHoraInicio = ofertas.get(p).getFechaHoraInicio();
            holder.fechaHoraFin = ofertas.get(p).getFechaHoraFin();
            holder.fotoCoche = ofertas.get(p).getFotoCoche();
            holder.matricula = ofertas.get(p).getMatricula();
        }

        holder.card_oferta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((getItemCount() - 1) == p && tiempo.equals("futuras")){
                    ((ComunidadElegida) activity).dialogoAnadirOferta();
                }else{
                    Intent intent = new Intent(context, OfertaElegida.class);

                    intent.putExtra("usuario", usuario);
                    intent.putExtra("idComunidad", idComunidad);
                    intent.putExtra("idCoche", ofertas.get(p).getIdCoche());
                    intent.putExtra("idOferta", ofertas.get(p).getIdOferta());

                    activity.startActivityForResult(intent, 2);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        // +1 por la card de añadir coche
        if(tiempo.equals("futuras")){
            return ofertas.size() + 1;
        }else{
            return ofertas.size();
        }
    }

    class OfertaHolder extends RecyclerView.ViewHolder{

        TextView matriculaCocheOfertaTextView, inicioOfertaTextView, finalOfertaTextView;
        ImageView imagenPreviaCocheOfertaImageView;
        Button anadirOfertaButton;
        CardView card_oferta;

        String idOferta, idCoche, idComunidad, fechaHoraInicio, fechaHoraFin, fotoCoche, matricula;

        public OfertaHolder(@NonNull View itemView) {
            super(itemView);
            imagenPreviaCocheOfertaImageView = itemView.findViewById(R.id.imagenPreviaCocheOfertaImageView);
            matriculaCocheOfertaTextView = itemView.findViewById(R.id.matriculaCocheOfertaTextView);
            inicioOfertaTextView = itemView.findViewById(R.id.inicioOfertaTextView);
            finalOfertaTextView = itemView.findViewById(R.id.finalOfertaTextView);
            anadirOfertaButton = itemView.findViewById(R.id.anadirOfertaButton);
            card_oferta = itemView.findViewById(R.id.card_ofertas);
        }
    }
}
