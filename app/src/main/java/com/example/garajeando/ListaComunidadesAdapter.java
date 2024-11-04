package com.example.garajeando;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.app.Activity;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ListaComunidadesAdapter extends ArrayAdapter<String> {

    private final Activity context;

    private final Activity activity;

    private ArrayList<Comunidad> comunidades=new ArrayList<Comunidad>();

    CardView l_comunidades;

    String usuario;



    public ListaComunidadesAdapter(Activity activity, Activity context, ArrayList<Comunidad> comunidades, String usuario) {
        super(context, R.layout.layout_comunidades, comunidades.stream().map(Comunidad::getNombre).collect(Collectors.toList()));
        this.context = context;
        this.activity = activity;
        this.comunidades=comunidades;
        this.usuario=usuario;
    }

    public View getView(int p, View view, ViewGroup parent) {
        LayoutInflater inf=context.getLayoutInflater();
        View l=inf.inflate(R.layout.layout_comunidades, parent,false);

        //obtener elementos de la vista
        TextView t_titulo = l.findViewById(R.id.nombreComunidadTextViewC);
        l_comunidades= l.findViewById(R.id.card_oferta_busqueda);

        //se muestran los elementos de la peliculas en la posicion que se ha pasado
        t_titulo.setText(comunidades.get(p).getNombre());

        //listener cuando se pulsa la comunidad
        l_comunidades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ComunidadElegida.class);
                intent.putExtra("idComunidad", comunidades.get(p).getIdComunidad());
                intent.putExtra("nombreComunidad", comunidades.get(p).getNombre());
                intent.putExtra("codInvitacion", comunidades.get(p).getCodInvitacion());
                intent.putExtra("rolComunidad", comunidades.get(p).getRol());
                intent.putExtra("usuario", usuario);

                activity.startActivityForResult(intent, 1);
            }
        });
        return l;
    }
}
