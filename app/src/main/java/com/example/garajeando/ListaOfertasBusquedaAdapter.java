package com.example.garajeando;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ListaOfertasBusquedaAdapter extends BaseAdapter{

    private Activity context;

    private Activity activity;

    private ArrayList<Oferta> ofertas=new ArrayList<Oferta>();

    CardView l_ofertas;

    String usuario, idComunidad, fechaHoraInicioBusqueda, fechaHoraFinalBusqueda;

    LayoutInflater inflater;

    public ListaOfertasBusquedaAdapter(Activity activity, Activity context, String usuario, String idComunidad, ArrayList<Oferta> ofertas, String fechaHoraInicioBusqueda, String fechaHoraFinalBusqueda){
        this.activity = activity;
        this.context = context;
        this.usuario = usuario;
        this.idComunidad = idComunidad;
        this.ofertas = ofertas;
        this.fechaHoraInicioBusqueda = fechaHoraInicioBusqueda;
        this.fechaHoraFinalBusqueda = fechaHoraFinalBusqueda;

        this.inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return ofertas.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.layout_ofertas_busqueda, null);

        TextView matriculaTextView = (TextView) view.findViewById(R.id.matriculaOfertaTextView);
        TextView fechaInicioTextView = (TextView) view.findViewById(R.id.fechaHoraInicioOfertaTextView);
        TextView fechaFinTextView = (TextView) view.findViewById(R.id.fechaHoraFinOfertaTextView);
        ImageView imagenCocheImageView = (ImageView) view.findViewById(R.id.imagenCocheOfertaImageView);

        matriculaTextView.setText(ofertas.get(i).getMatricula());
        fechaInicioTextView.setText(ofertas.get(i).getFechaHoraInicio());
        fechaFinTextView.setText(ofertas.get(i).getFechaHoraFin());

        Glide.with(this.context).load("http://ec2-51-20-10-72.eu-north-1.compute.amazonaws.com/imagenes/fotoscoches/"+ofertas.get(i).getFotoCoche()).into(imagenCocheImageView);

        l_ofertas = view.findViewById(R.id.card_oferta_busqueda);

        l_ofertas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OfertaElegida.class);
                intent.putExtra("idComunidad", idComunidad);
                intent.putExtra("usuario", usuario);
                intent.putExtra("idCoche", ofertas.get(i).getIdCoche());
                intent.putExtra("idOferta", ofertas.get(i).getIdOferta());
                intent.putExtra("fechaHoraInicio", fechaHoraInicioBusqueda);
                intent.putExtra("fechaHoraFin", fechaHoraFinalBusqueda);

                activity.startActivityForResult(intent, 1);
            }
        });

        return view;
    }
}
