package com.example.garajeando;

import android.content.Context;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CocheElegido extends AppCompatActivity {

    private String usuario, idComunidad;

    String idCoche, propietario, matricula, marca, modelo, transmision, combustible, descripcion;
    Integer plazas, puertas;
    Boolean aireAcondicionado, bluetooth, gps;

    private GridView fotosGridView;
    private String[] nombreFotosCoche = new String[10];
    private static String URL_BASE_FOTOS = "http://ec2-51-20-10-72.eu-north-1.compute.amazonaws.com/imagenes/fotoscarnet/";
    private FotosCocheAdapter fotosCocheAdapter;
    private Context context = this;
    private Integer numFotos;
    private JSONArray respuestaFotos;

    ImageView imagenPrincipalImageView;
    String nombreFotoPrincipal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_coche_elegido);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usuario = getIntent().getExtras().getString("usuario");
        idComunidad = getIntent().getExtras().getString("idComunidad");

        idCoche = getIntent().getExtras().getString("idCoche");

        obtenerFotosCoche();

        propietario = getIntent().getExtras().getString("propietario");
        matricula = getIntent().getExtras().getString("matricula");
        marca = getIntent().getExtras().getString("marca");
        modelo = getIntent().getExtras().getString("modelo");
        plazas = getIntent().getExtras().getInt("plazas");
        puertas = getIntent().getExtras().getInt("puertas");
        transmision = getIntent().getExtras().getString("transmision");
        combustible = getIntent().getExtras().getString("combustible");
        aireAcondicionado = getIntent().getExtras().getBoolean("aireacondicionado");
        bluetooth = getIntent().getExtras().getBoolean("bluetooth");
        gps = getIntent().getExtras().getBoolean("gps");
        descripcion = getIntent().getExtras().getString("descripcion");

        imagenPrincipalImageView = (ImageView) findViewById(R.id.imagenPrincipalCocheImageView);

        fotosGridView = (GridView) findViewById(R.id.imagenesSecundariasCocheGridView);

    }

    private void obtenerFotosCoche(){
        StringRequest peticion = new StringRequest(Request.Method.POST,
                Constantes.URL_OBTENERFOTOSCOCHE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respuesta) {
                        try {
                            JSONObject objetoJSON = new JSONObject(respuesta);
                            numFotos = Integer.parseInt(String.valueOf(objetoJSON.getJSONArray("mensaje").length()));

                            limpiarArrayListFotos();
                            respuestaFotos = objetoJSON.getJSONArray("mensaje");
                            guardarFotos();

                            fotosCocheAdapter = new FotosCocheAdapter(context, nombreFotosCoche, numFotos);
                            fotosGridView.setAdapter(fotosCocheAdapter);
                            //fotosCocheAdapter.notifyDataSetChanged();

                            //se crea el adaptar propio
                            //adapterCo = new ListaCochesAdapter(activity, activity, p_ids, p_propietarios, p_matriculas, p_marcas, p_modelos, p_plazas, p_puertas, p_transmisiones, p_combustibles, p_airesacondicionados, p_bluetooths, p_gpss, p_descripciones, usuario);
                            //l_coches = (ListView) findViewById(R.id.listaCochesListView);
                            //l_coches.setAdapter(adapterCo);

                            //cuando se modifican datos notificar para que actualice
                            //adapterCo.notifyDataSetChanged();

                            AdministradorPeticiones.getInstance(context).cancelAll("peticion");
                        } catch (JSONException e) {
                            //throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //
                    }
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("IdCoche", idCoche);

                return parametros;
            }
        };

        peticion.setTag("peticion");
        AdministradorPeticiones.getInstance(this).addToRequestQueue(peticion);
    }

    private void limpiarArrayListFotos(){
        imagenPrincipalImageView.setImageResource(R.drawable.coche);
        nombreFotosCoche = new String[9];
    }

    private void guardarFotos(){
        try{

            nombreFotoPrincipal = respuestaFotos.getJSONObject(0).getString("FotoCoche");
            Glide.with(this.context).load("http://ec2-51-20-10-72.eu-north-1.compute.amazonaws.com/imagenes/fotoscarnet/"+nombreFotoPrincipal).into(imagenPrincipalImageView);
            for (int i = 1; i < respuestaFotos.length(); i++)
            {
                JSONObject jsonCoches = respuestaFotos.getJSONObject(i);

                nombreFotosCoche[i] = jsonCoches.getString("FotoCoche");
            }
        }catch (Exception e){
            //no hace nada
        }
    }
}