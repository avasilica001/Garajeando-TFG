package com.example.garajeando;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class FotosCocheAdapter extends BaseAdapter {

    private String[] nombreFotos;
    private Context context;
    private LayoutInflater layoutInflater;
    Integer numFotos;

    public FotosCocheAdapter(Context context, String[] nombreFotos, Integer numFotos) {
        this.nombreFotos = nombreFotos;
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        this.numFotos=numFotos;
    }

    @Override
    public int getCount() {
        return numFotos;
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
        if (view == null){
            view = layoutInflater.inflate(R.layout.imagen_coche, viewGroup, false);
        }

        ImageView imagen = (ImageView) view.findViewById(R.id.imagenCocheImageView);

        if(numFotos == 9 && (nombreFotos[i].isEmpty() || nombreFotos[i] == null)){
            imagen.setImageResource(R.drawable.coche);
        }else{
            Glide.with(this.context).load("http://ec2-51-20-10-72.eu-north-1.compute.amazonaws.com/imagenes/fotoscarnet/"+nombreFotos[i]).into(imagen);
        }

        return view;
    }
}
