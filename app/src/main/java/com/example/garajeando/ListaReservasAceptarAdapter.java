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

public class ListaReservasAceptarAdapter extends RecyclerView.Adapter<ListaReservasAceptarAdapter.ReservaHolder> {

    private final Activity context;

    private final Activity activity;

    //arraylist para cada columna en la bd
    private ArrayList<Reserva> reservas=new ArrayList<Reserva>();

    String usuario, idComunidad;

    public ListaReservasAceptarAdapter(Activity activity, Activity context, ArrayList<Reserva> reservas, String usuario, String idComunidad) {
        this.context = context;
        this.activity = activity;
        this.reservas = reservas;
        this.usuario = usuario;
        this.idComunidad = idComunidad;
    }

    @NonNull
    @Override
    public ListaReservasAceptarAdapter.ReservaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View l = LayoutInflater.from(context).inflate(R.layout.layout_reservas, parent, false);

        return new ListaReservasAceptarAdapter.ReservaHolder(l);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaReservasAceptarAdapter.ReservaHolder holder, @SuppressLint("RecyclerView") int p) {
        holder.matriculaCocheReservaTextView.setText(String.valueOf(reservas.get(p).matricula));
        holder.inicioReservaTextView.setText(String.valueOf("Inicio: " + reservas.get(p).fechaHoraInicio));
        holder.finalReservaTextView.setText(String.valueOf("Fin: " + reservas.get(p).fechaHoraFin));
        Glide.with(this.context).load("http://ec2-51-20-10-72.eu-north-1.compute.amazonaws.com/imagenes/fotoscoches/"+ reservas.get(p).fotoCoche).into(holder.imagenPreviaCocheReservaImageView);

        holder.idReserva = reservas.get(p).getIdReserva();
        holder.idCoche = reservas.get(p).getIdCoche();
        holder.idComunidad = reservas.get(p).getIdComunidad();
        holder.fechaHoraInicio = reservas.get(p).getFechaHoraInicio();
        holder.fechaHoraFin = reservas.get(p).getFechaHoraFin();
        holder.fotoCoche = reservas.get(p).getFotoCoche();
        holder.matricula = reservas.get(p).getMatricula();
        holder.aprobada = reservas.get(p).getAprobada();
        holder.nombreApellidos = reservas.get(p).getNombreApellidos();
        holder.propietario = reservas.get(p).getPropietario();
        holder.usuarioReserva = reservas.get(p).getIdUsuario();

        holder.usuarioReservaTextView.setText(String.valueOf(holder.nombreApellidos));

        holder.card_reserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReservaElegida.class);

                intent.putExtra("idReserva", reservas.get(p).getIdReserva());
                intent.putExtra("usuario", usuario);
                intent.putExtra("idComunidad", idComunidad);
                intent.putExtra("idCoche", reservas.get(p).getIdCoche());

                activity.startActivityForResult(intent, 2);

            }
        });
    }

    @Override
    public int getItemCount() {
        return reservas.size();
    }

    class ReservaHolder extends RecyclerView.ViewHolder{

        TextView matriculaCocheReservaTextView, inicioReservaTextView, finalReservaTextView, usuarioReservaTextView;
        ImageView imagenPreviaCocheReservaImageView;
        CardView card_reserva;

        String idReserva, idCoche, idComunidad, fechaHoraInicio, fechaHoraFin, fotoCoche, matricula, aprobada, usuarioReserva, propietario, nombreApellidos;

        public ReservaHolder(@NonNull View itemView) {
            super(itemView);
            imagenPreviaCocheReservaImageView = itemView.findViewById(R.id.imagenPreviaCocheReservaImageView);
            matriculaCocheReservaTextView = itemView.findViewById(R.id.matriculaCocheReservaTextView);
            inicioReservaTextView = itemView.findViewById(R.id.inicioReservaTextView);
            finalReservaTextView = itemView.findViewById(R.id.finalReservaTextView);
            usuarioReservaTextView = itemView.findViewById(R.id.usuarioReservaTextView);
            card_reserva = itemView.findViewById(R.id.card_reserva);
        }
    }
}
