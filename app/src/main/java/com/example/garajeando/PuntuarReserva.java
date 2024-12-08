package com.example.garajeando;

import static android.icu.lang.UCharacter.toUpperCase;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class PuntuarReserva extends AppCompatActivity {

    private Activity activity = this;
    private Context context = this;

    private String usuario, idComunidad, idUsuarioReserva, idReserva, idCoche, nombreFotoPrincipal, fechaHoraInicio, fechaHoraFin, combustible, matricula, tiempoLondon;

    private ImageView imagenPrincipalCocheResenaImageView;
    private RadioGroup estadoExteriorRadioGroup, estadoInteriorRadioGroup;
    private RadioButton sinDanosRadioButton, rallonesRadioButton, abolladoRadioButton, IgualRadioButton, sucioDesordenadoRadioButton;
    private CheckBox devueltoATiempoExteriorCheckBox, aparcadoCorrectamenteCheckBox, exteriorLimpiadoCheckBox, interiorLimpiadoCheckBox, depositoLlenadoCheckBox;
    private EditText minutosTardeEditText, litrosDepositoEditText;
    private TextView avisoResenaTextView;
    private Button guardarResenaButton;

    private static final String URL_BASE_FOTOS = "http://ec2-51-20-10-72.eu-north-1.compute.amazonaws.com/imagenes/fotoscoches/";

    private JSONArray respuestaFotos, respuestaReserva, respuestaCoche;

    Integer puntos, minutos;

    private TimeZone zonaHorariaLondres = TimeZone.getTimeZone("Europe/London");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Preferencias.aplicarTema(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_puntuar_reserva);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usuario = getIntent().getExtras().getString("usuario");
        idComunidad = getIntent().getExtras().getString("idComunidad");
        idUsuarioReserva = getIntent().getExtras().getString("idUsuarioReserva");
        idReserva = getIntent().getExtras().getString("idReserva");
        idCoche = getIntent().getExtras().getString("idCoche");

        setSupportActionBar(findViewById(R.id.puntuarReservaToolbar));
        getSupportActionBar().setTitle("PUNTUAR RESERVA");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imagenPrincipalCocheResenaImageView = findViewById(R.id.imagenPrincipalCocheResenaImageView);

        estadoExteriorRadioGroup = findViewById(R.id.estadoExteriorRadioGroup);
        sinDanosRadioButton = findViewById(R.id.sinDanosRadioButton);
        rallonesRadioButton = findViewById(R.id.rallonesRadioButton);
        abolladoRadioButton = findViewById(R.id.abolladoRadioButton);

        estadoInteriorRadioGroup = findViewById(R.id.estadoInteriorRadioGroup);
        IgualRadioButton = findViewById(R.id.IgualRadioButton);
        sucioDesordenadoRadioButton = findViewById(R.id.sucioDesordenadoRadioButton);

        devueltoATiempoExteriorCheckBox = findViewById(R.id.devueltoATiempoExteriorCheckBox);
        minutosTardeEditText = findViewById(R.id.minutosTardeEditText);

        aparcadoCorrectamenteCheckBox = findViewById(R.id.aparcadoCorrectamenteCheckBox);

        exteriorLimpiadoCheckBox = findViewById(R.id.exteriorLimpiadoCheckBox);

        interiorLimpiadoCheckBox = findViewById(R.id.interiorLimpiadoCheckBox);

        depositoLlenadoCheckBox = findViewById(R.id.depositoLlenadoCheckBox);
        litrosDepositoEditText = findViewById(R.id.litrosDepositoEditText);

        avisoResenaTextView = findViewById(R.id.avisoResenaTextView);
        avisoResenaTextView.setVisibility(View.GONE);

        guardarResenaButton = findViewById(R.id.guardarResenaButton);

        guardarResenaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(camposRellenadosCorrectamente()){
                    calcularPuntos();
                }
            }
        });


        obtenerInfoReserva();

        OnBackPressedCallback volverActividadAnterior = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intentResultado = new Intent();
                setResult(3, intentResultado);
                finish();
            }
        };

        getOnBackPressedDispatcher().addCallback(this, volverActividadAnterior);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Adjust visibility based on conditions
        menu.findItem(R.id.BuscarToolbarItem).setVisible(false);
        //menu.findItem(R.id.PerfilToobarItem).setVisible(condition2);
        //menu.findItem(R.id.PreferenciasToobarItem).setVisible(condition3);
        menu.findItem(R.id.AdministradorToobarItem).setVisible(false);
        //menu.findItem(R.id.CerrarSesionToobarItem).setVisible(CerrarSesionToobarItem);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.PerfilToobarItem) {
            Intent intentPerfil = new Intent(PuntuarReserva.this, PerfilUsuario.class);
            intentPerfil.putExtra("usuario", usuario);
            intentPerfil.putExtra("idComunidad", idComunidad);
            intentPerfil.putExtra("idUsuarioPerfil", usuario);
            startActivityForResult(intentPerfil,1);
            return true;
        } else if (itemId == R.id.TemaToobarItem) {
            boolean esOscuro = Preferencias.esTemaOscuro(this);
            Preferencias.setTemaOscuro(this, !esOscuro);
            Intent intentTema = getIntent();
            finish();
            startActivity(intentTema);
            return true;
        } else if (itemId == R.id.CerrarSesionToobarItem) {
            Intent intentCerrarSesion = new Intent(PuntuarReserva.this, IniciarSesion.class);
            // Clear all activities in the current task stack
            intentCerrarSesion.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intentCerrarSesion);
            finish();
            return true;
        }

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void obtenerInfoReserva(){
        String idCocheEncoded = "";
        String idReservaEncoded = "";
        try {
            idCocheEncoded = URLEncoder.encode(idCoche, "UTF-8");
            idReservaEncoded = URLEncoder.encode(idReserva, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
        }

        StringRequest peticion = new StringRequest(Request.Method.GET,
                Constantes.URL_OBTENERINFORESERVA+"?IdCoche="+idCocheEncoded+"&IdReserva="+idReservaEncoded,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respuesta) {
                        try {
                            JSONObject objetoJSON = new JSONObject(respuesta);

                            respuestaCoche = objetoJSON.getJSONArray("Coche");

                            respuestaReserva = objetoJSON.getJSONArray("Reserva");

                            respuestaFotos = objetoJSON.getJSONArray("Fotos");
                            guardarInfo();

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
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        peticion.setTag("peticion");
        AdministradorPeticiones.getInstance(this).addToRequestQueue(peticion);
    }

    private void guardarInfo(){
        try{
            nombreFotoPrincipal = respuestaFotos.getJSONObject(0).getString("FotoCoche");
            Glide.with(this.context).load(URL_BASE_FOTOS+nombreFotoPrincipal).into(imagenPrincipalCocheResenaImageView);
            fechaHoraInicio = respuestaReserva.getJSONObject(0).getString("FechaHoraInicio");
            fechaHoraFin = respuestaReserva.getJSONObject(0).getString("FechaHoraFin");

            combustible = respuestaCoche.getJSONObject(0).getString("Combustible");
            matricula = respuestaCoche.getJSONObject(0).getString("Matricula");

        }catch (Exception e){
            //no hace nada
        }
    }

    private void calcularPuntos(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime inicio = LocalDateTime.parse(fechaHoraInicio, formatter);
        LocalDateTime fin = LocalDateTime.parse(fechaHoraFin, formatter);

        minutos = Math.toIntExact(Duration.between(inicio, fin).toMinutes());

        puntos = 0;

        if(rallonesRadioButton.isChecked()){
            puntos = puntos - (minutos);
        }else if(abolladoRadioButton.isChecked()){
            puntos = puntos - (minutos*2);
        }

        if(sucioDesordenadoRadioButton.isChecked()){
            puntos = puntos - (minutos/4);
        }

        if(!devueltoATiempoExteriorCheckBox.isChecked()){
            if(!exteriorLimpiadoCheckBox.isChecked() && !interiorLimpiadoCheckBox.isChecked() && !depositoLlenadoCheckBox.isChecked()){
                puntos = puntos - Integer.valueOf(minutosTardeEditText.getText().toString().trim());
            }
        }else{
            puntos = puntos + (minutos/4);
        }

        if(!aparcadoCorrectamenteCheckBox.isChecked()){
            puntos = puntos - (minutos/6);
        }else{
            puntos = puntos + (minutos/4);
        }

        if(exteriorLimpiadoCheckBox.isChecked()){
            puntos = puntos + (minutos/4);
        }

        if(interiorLimpiadoCheckBox.isChecked()){
            puntos = puntos + (minutos/4);
        }

        if(depositoLlenadoCheckBox.isChecked()){
            if(combustible.equals("Eléctrico")){
                puntos = puntos + ((Integer.valueOf(litrosDepositoEditText.getText().toString().trim())));
            }else{
                puntos = puntos + ((Integer.valueOf(litrosDepositoEditText.getText().toString().trim()))*5);
            }
        }

        String formato = "yyyy-MM-dd HH:mm:ss";

        // Create a SimpleDateFormat instance with the desired format
        SimpleDateFormat dateFormato = new SimpleDateFormat(formato);

        // Get the current date and time
        Date dateAhora = new Date();

        // Set the formatter to London's timezone
        dateFormato.setTimeZone(TimeZone.getTimeZone("Europe/London"));

        // Format the current date and time in London's timezone
        tiempoLondon = dateFormato.format(dateAhora);


        AlertDialog.Builder builder = new AlertDialog.Builder(PuntuarReserva.this);
        builder.setTitle("Elige una opción")
                .setMessage("¿Estás seguro de que quieres guardar?")

                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogo, int which) {
                        subirPuntos();
                    }
                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogo, int which) {
                        dialogo.dismiss();
                    }
                });

        Preferencias.setTemaAlertDialogPositivoNegativo(builder,context);


    }

    private Boolean camposRellenadosCorrectamente(){
        Boolean correctos = true;

        avisoResenaTextView.setVisibility(View.GONE);
        if(depositoLlenadoCheckBox.isChecked() && litrosDepositoEditText.getText().toString().trim().equals("")){
            correctos = false;
            avisoResenaTextView.setVisibility(View.VISIBLE);
            avisoResenaTextView.setText("Introduce los litros aproximados de combustible repostados");
        }

        if(!devueltoATiempoExteriorCheckBox.isChecked() && minutosTardeEditText.getText().toString().trim().equals("")){
            correctos = false;
            avisoResenaTextView.setVisibility(View.VISIBLE);
            avisoResenaTextView.setText("Introduce los minutos aproximados de tiempo tarde para devolver el coche");
        }

        if(!IgualRadioButton.isChecked() && !sucioDesordenadoRadioButton.isChecked()){
            correctos = false;
            avisoResenaTextView.setVisibility(View.VISIBLE);
            avisoResenaTextView.setText("Selecciona una opción para los daños interiores");
        }

        if(!sinDanosRadioButton.isChecked() && !rallonesRadioButton.isChecked() && !abolladoRadioButton.isChecked()){
            correctos = false;
            avisoResenaTextView.setVisibility(View.VISIBLE);
            avisoResenaTextView.setText("Selecciona una opción para los daños exteriores");
        }

        return correctos;
    }

    private void subirPuntos(){
        StringRequest peticion = new StringRequest(Request.Method.POST,
                Constantes.URL_GUARDARPUNTOSRESENA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respuesta) {
                        try {
                            JSONObject objetoJSON = new JSONObject(respuesta);


                            if(objetoJSON.getString("error").equals("false")){
                                Intent intentResultado = new Intent();
                                setResult(3, intentResultado);
                                finish();
                            }

                            AdministradorPeticiones.getInstance(context).cancelAll("peticion");
                        } catch (JSONException e) {
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
                parametros.put("IdUsuario", usuario);
                parametros.put("Matricula", toUpperCase(matricula));
                parametros.put("IdReserva", idReserva);
                parametros.put("IdUsuarioReserva", idUsuarioReserva);
                parametros.put("Fecha", tiempoLondon);
                parametros.put("Puntos", String.valueOf(puntos));
                parametros.put("Minutos", String.valueOf(minutos));
                return parametros;
            }
        };

        peticion.setTag("peticion");
        AdministradorPeticiones.getInstance(this).addToRequestQueue(peticion);
    }
}