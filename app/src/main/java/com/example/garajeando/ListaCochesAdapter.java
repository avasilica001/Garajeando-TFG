package com.example.garajeando;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.app.Activity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.google.common.math.PairedStatsAccumulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.POST;

public class ListaCochesAdapter extends RecyclerView.Adapter<ListaCochesAdapter.CocheHolder> {

    private final Activity context;

    private Activity activity;

    //arraylist para cada columna en la bd
    private ArrayList<Coche> coches=new ArrayList<Coche>();
    private ArrayList<String> idCoches = new ArrayList<String>();
    private ArrayList<String> matriculas = new ArrayList<String>();

    CardView l_coches;

    String usuario, idComunidad;
    Integer numCochesOtrasComunidades;
    JSONArray respuestaComunidades;

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
            Glide.with(this.context).load("http://ec2-51-20-10-72.eu-north-1.compute.amazonaws.com/imagenes/fotoscarnet/IMG_20240910_170324_1.png").into(holder.imagenCocheImageView);

            holder.id = coches.get(p).getIdCoche();
            holder.propietario = coches.get(p).getPropietario();
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

        String id, propietario, matricula, marca, modelo, transmision, combustible, descripcion;
        Integer plazas, puertas;
        Boolean aireacondicionado, bluetooth, gps;

        public CocheHolder(@NonNull View itemView) {
            super(itemView);
            matriculaTextView = (TextView) itemView.findViewById(R.id.matriculaCocheTextView);
            imagenCocheImageView = (ImageView) itemView.findViewById(R.id.imagenPreviaCocheImageView);
            anadirCocheButton = (Button) itemView.findViewById(R.id.anadirCocheButton);
            card_coche = (CardView) itemView.findViewById(R.id.card_coche);
        }
    }
}