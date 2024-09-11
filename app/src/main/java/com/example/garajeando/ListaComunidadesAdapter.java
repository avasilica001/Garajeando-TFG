package com.example.garajeando;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.app.Activity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.ArrayList;

public class ListaComunidadesAdapter extends ArrayAdapter<String> {

    private final Activity context;

    private Activity activity;

    //arraylist para cada columna en la bd
    private ArrayList<String> ids=new ArrayList<>();
    private ArrayList<String> nombres=new ArrayList<>();
    private ArrayList<String> codInvitaciones=new ArrayList<>();
    private ArrayList<String> roles=new ArrayList<>();

    CardView l_comunidades;

    String usuario;



    public ListaComunidadesAdapter(Activity activity, Activity context, ArrayList<String> ids, ArrayList<String> nombres, ArrayList<String> codInvitaciones, ArrayList<String> roles, String usuario) {
        super(context, R.layout.layout_comunidades, nombres);
        this.context = context;
        this.activity = activity;
        this.ids=ids;
        this.nombres=nombres;
        this.codInvitaciones=codInvitaciones;
        this.roles=roles;
    }

    public View getView(int p, View view, ViewGroup parent) {
        LayoutInflater inf=context.getLayoutInflater();
        View l=inf.inflate(R.layout.layout_comunidades, parent,false);

        //obtener elementos de la vista
        TextView t_titulo = l.findViewById(R.id.nombreComunidadTextViewC);
        l_comunidades= l.findViewById(R.id.card_comunidad);

        //se muestran los elementos de la peliculas en la posicion que se ha pasado
        t_titulo.setText(String.valueOf(nombres.get(p)));

        //listener cuando se pulsa la pelicula
        l_comunidades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //se pasan todos los datos para ver la pelicula
                Intent intent = new Intent(context, ComunidadElegida.class);
                intent.putExtra("idComunidad", String.valueOf(ids.get(p)));
                intent.putExtra("nombreComunidad", String.valueOf(nombres.get(p)));
                intent.putExtra("codInvitacion", String.valueOf(codInvitaciones.get(p)));
                intent.putExtra("rolComunidad", String.valueOf(roles.get(p)));
                intent.putExtra("usuario", usuario);

                activity.startActivityForResult(intent, 1);
            }
        });
        return l;
    };
}
