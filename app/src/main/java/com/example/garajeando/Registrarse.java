package com.example.garajeando;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import android.Manifest;

public class Registrarse extends AppCompatActivity {

    private EditText correoElectronicoEditText, contrasenaEditText, repetirContrasenaEditText, nombreEditText, apellidosEditText, direccionEditText;
    private Button registrarseButton, anadirFotoButton, anadirCarnetFrontal, anadirCarnetReverso;
    private TextView avisoTextView;
    private ImageView imagenPerfil;
    Toolbar barraSuperiorRegistrarseToolbar;

    private String correoElectronico, contrasena, repetirContrasena, nombre, apellidos, aviso, contrasenaEncriptada, pathImagen, direccion;
    Uri uriImagen;

    public static Boolean contrasenaVisible;

    private Uri imagen, perfil;
    private final int F_PERFIL = 0;
    private int target;

    private Bitmap bperfil, bfrontal, breverso;
    private String b64p;

    private static final int IMAGE_CODE=112;
    private static final int PHOTO_CODE=111;

    ActivityResultLauncher<String> activityResultLauncherElegirFoto;
    ActivityResultLauncher<Uri> activityResultLauncherSacarFoto;

    Intent camarai;


    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Preferencias.aplicarTema(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrarse);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.RegistroLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Setear barra superior
        setSupportActionBar(findViewById(R.id.registrarseToolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        correoElectronicoEditText = findViewById(R.id.correoElectronicoEditTextR);
        contrasenaEditText = findViewById(R.id.contrasenaEditTextR);
        contrasenaVisible = false;
        repetirContrasenaEditText = findViewById(R.id.repetirContrasenaEditText);
        nombreEditText = findViewById(R.id.nombreEditText);
        apellidosEditText = findViewById(R.id.apellidosEditText);
        direccionEditText = findViewById(R.id.direccionEditText);;

        avisoTextView = findViewById(R.id.avisoTextViewR);

        registrarseButton = findViewById(R.id.registrarseButtonR);
        registrarseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                crearUsuario();
            }
        });

        imagenPerfil = findViewById(R.id.fotoPerfilImageViewR);

        activityResultLauncherSacarFoto = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        // There are no request code
                        if (result) {
                            try {

                                Toast.makeText(Registrarse.this, "Se ha guardado la imagen en la galería", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                //no hace nada
                            }
                        }
                        Intent intent2 = new Intent(Registrarse.this, RecortarImagen.class);
                        intent2.putExtra("data", String.valueOf(imagen));
                        if(target == F_PERFIL){
                            intent2.putExtra("formato", "11");
                        }else{
                            intent2.putExtra("formato", "169");
                        }
                        startActivityForResult(intent2,101);
                    }
                });


        activityResultLauncherElegirFoto = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri data) {
                        if (data != null && !data.equals(Uri.EMPTY)){
                            imagen = data;
                            //imagenPerfil.setImageURI(imagen);

                            Intent intent2 = new Intent(Registrarse.this, RecortarImagen.class);
                            intent2.putExtra("data", String.valueOf(imagen));
                            if(target == F_PERFIL){
                                intent2.putExtra("formato", "11");
                            }else{
                                intent2.putExtra("formato", "169");
                            }
                            startActivityForResult(intent2,101);
                        }
                    }
                });

        anadirFotoButton = findViewById(R.id.anadirFotoPerfilButtonR);
        anadirFotoButton.bringToFront();
        anadirFotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                target = F_PERFIL;
                mostrarDialogoSeleccion();
            }
        });

        contrasenaEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= contrasenaEditText.getRight() - contrasenaEditText.getCompoundDrawables()[2].getBounds().width() - 60) {
                        int seleccion = contrasenaEditText.getSelectionEnd();
                        if (contrasenaVisible) {
                            contrasenaEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.candado_cerrado, 0, R.drawable.ojo_cerrado, 0);
                            contrasenaEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            contrasenaVisible = false;
                        } else {
                            contrasenaEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.candado_cerrado, 0, R.drawable.ojo_abierto, 0);
                            contrasenaEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            contrasenaVisible = true;
                        }
                        contrasenaEditText.setSelection(seleccion);
                        return true;
                    }
                }

                return false;
            }
        });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        aviso = avisoTextView.getText().toString().trim();
        outState.putString("aviso", aviso);
        outState.putBoolean("avisoVisible",avisoTextView.getVisibility() == View.VISIBLE);
        outState.putBoolean("contrasenaVisible", contrasenaVisible);
        contrasenaEditText = findViewById(R.id.contrasenaEditTextR);

        if(perfil != null && !perfil.equals(Uri.EMPTY)) {
            outState.putParcelable("perfil", perfil);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        aviso = savedInstanceState.getString("aviso");
        avisoTextView.setText(String.valueOf(aviso));
        if(savedInstanceState.getBoolean("avisoVisible")){avisoTextView.setVisibility(View.VISIBLE);}

        contrasenaVisible = savedInstanceState.getBoolean("contrasenaVisible");
        int seleccion = contrasenaEditText.getSelectionEnd();
        if (contrasenaVisible) {
            contrasenaEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.candado_cerrado, 0, R.drawable.ojo_abierto, 0);
            contrasenaEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            contrasenaVisible = true;
        } else {
            contrasenaEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.candado_cerrado, 0, R.drawable.ojo_cerrado, 0);
            contrasenaEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            contrasenaVisible = false;
        }
        contrasenaEditText.setSelection(seleccion);
        contrasenaEditText = findViewById(R.id.contrasenaEditTextR);

        perfil = savedInstanceState.getParcelable("perfil");
        if(perfil != null && !perfil.equals(Uri.EMPTY)) {
            imagenPerfil.setImageURI(savedInstanceState.getParcelable("perfil"));
        }
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
        menu.findItem(R.id.PerfilToobarItem).setVisible(false);
        menu.findItem(R.id.TemaToobarItem).setVisible(true);
        menu.findItem(R.id.AdministradorToobarItem).setVisible(false);
        menu.findItem(R.id.CerrarSesionToobarItem).setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.TemaToobarItem) {
            boolean esOscuro = Preferencias.esTemaOscuro(this);
            Preferencias.setTemaOscuro(this, !esOscuro);
            Intent intentTema = getIntent();
            finish();
            startActivity(intentTema);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void mostrarDialogoSeleccion(){
        String[] opciones = {"Sacar una foto","Seleccionar de la galería"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona una opción para tu foto de perfil");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0){
                    //se ha de mirar primero si la version es igual o superior a la marshmallow
                    //si es igual o superior
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        //si no hay permisos para acceder a la cámara, o escribir
                        if (ActivityCompat.checkSelfPermission(Registrarse.this,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(Registrarse.this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            //dar permisos de camara y escritura
                            ActivityCompat.requestPermissions(Registrarse.this,new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PHOTO_CODE);
                        } else {
                            //si ya hay permisos se abre la camara
                            abrirCamara();
                        }
                    }
                    //si la version es menor a la marshmallow
                    else {
                        abrirCamara();
                    }
                }else if (i == 1){


                    //mirar si la version es mayor o igual a marshmallow
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        //si lo es mirar si se han dado los permisos de lectura
                        if (ActivityCompat.checkSelfPermission(Registrarse.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            //si no hay permisos de lectura darselos
                            ActivityCompat.requestPermissions(Registrarse.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_CODE);
                        } else {
                            //si ya hay permisos
                            elegirfoto();
                        }
                    } else {
                        //si la version es menor a marshmallow
                        elegirfoto();
                    }
                }
            }
        });

        Preferencias.setTemaAlertDialogConOpciones(builder, this);
    }

    //metodo para cuando se usa la camara
    private void abrirCamara() {
        ContentValues cv = new ContentValues();
        //informacion de la imagen
        cv.put(MediaStore.Images.Media.TITLE, "Nueva Imagen");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Nueva Imagen sacada con la cámara");
        //uri de la imagen
        imagen = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
        //crear intent para la camara
        Intent camarai = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camarai.putExtra(MediaStore.EXTRA_OUTPUT, imagen);

        activityResultLauncherSacarFoto.launch(imagen);
    }

    //metodo para cuando se elige foto de la galeria
    private void elegirfoto() {
        activityResultLauncherElegirFoto.launch("image/*");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //si se esta sacando una foto
        if(requestCode==PHOTO_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //se han dado los permisos necesarios
                abrirCamara();
            } else {
                //no se han dado los permisos necesarios
                Toast.makeText(this, "No se han aceptado los permisos", Toast.LENGTH_SHORT).show();
            }
        }
        //si se esta eligiendo una foto de la galeria
        if(requestCode==IMAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //se han dado los permisos necesarios
                elegirfoto();
            } else {
                //no se han dado los permisos necesarios
                Toast.makeText(this, "No se han aceptado los permisos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void crearUsuario(){
        correoElectronico = correoElectronicoEditText.getText().toString().trim();
        contrasena = contrasenaEditText.getText().toString().trim();
        nombre = nombreEditText.getText().toString().trim();
        apellidos = apellidosEditText.getText().toString().trim();
        direccion = direccionEditText.getText().toString().trim();

        if (validarCredencialesR()) {
            try {
                contrasenaEncriptada = Encriptador.encrypt(contrasena);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            peticionCrearUsuario();
            finish();
        }
    }

    private void peticionCrearUsuario() {
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();

        //para la imagen de uri a bitmap
        try {

            if(perfil != null){
                bperfil = MediaStore.Images.Media.getBitmap(getContentResolver(),perfil);

                bperfil.compress(Bitmap.CompressFormat.PNG, 100, baos1);
                byte[] b1= baos1.toByteArray();
                b64p = Base64.encodeToString(b1, Base64.DEFAULT);
            }
        } catch (IOException e) {
            //no hace nada
        }

        String tiempo = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String nombreImagenFinal = "IMG_" + tiempo + "_" + F_PERFIL +".png";

        StringRequest peticion = new StringRequest(Request.Method.POST,
                Constantes.URL_CREARUSUARIO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String respuesta) {
                        avisoTextView.setVisibility(View.VISIBLE);

                        try {
                            JSONObject objetoJSON = new JSONObject(respuesta);
                            avisoTextView.setText(objetoJSON.getString("mensaje"));
                        } catch (JSONException e) {
                            //
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        avisoTextView.setVisibility(View.VISIBLE);
                        avisoTextView.setText("ERROR");

                    }
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("CorreoElectronico", correoElectronico);
                parametros.put("Contrasena", contrasenaEncriptada);
                parametros.put("Nombre", nombre);
                parametros.put("Apellidos", apellidos);
                parametros.put("Direccion", direccion);
                if (perfil != null){
                    parametros.put("FotoPerfil", b64p);
                    parametros.put("NombreFoto", nombreImagenFinal);
                }

                return parametros;
            }
        };

        AdministradorPeticiones.getInstance(this).addToRequestQueue(peticion);
    }

    private Boolean validarCredencialesR() {
        correoElectronico = correoElectronicoEditText.getText().toString().trim();
        contrasena = contrasenaEditText.getText().toString().trim();
        repetirContrasena = repetirContrasenaEditText.getText().toString().trim();
        nombre = nombreEditText.getText().toString().trim();
        apellidos = apellidosEditText.getText().toString().trim();
        direccion = direccionEditText.getText().toString().trim();

        Boolean camposValidos = true;

        if (!contrasena.isEmpty() && Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-._¿¡]).{8,}$").matcher(contrasena).matches()) {
            /*At least one upper case English letter, (?=.*?[A-Z])
            At least one lower case English letter, (?=.*?[a-z])
            At least one digit, (?=.*?[0-9])
            At least one special character, (?=.*?[#?!@$%^&*-])
            Minimum eight in length .{8,} (with the anchors)*/
            //contrasena correcta
            if (contrasena.equals(repetirContrasena)) {
                //contrasenas coinciden
            } else {
                //no coinciden
                camposValidos = false;
                avisoTextView.setVisibility(View.VISIBLE);
                avisoTextView.setText("Contraseñas no coinciden entre sí.");
            }
        } else {
            //contrasena incorrecta
            camposValidos = false;
            avisoTextView.setVisibility(View.VISIBLE);
            avisoTextView.setText("La contraseña no posee el formato correcto.");
        }

        if (!correoElectronico.isEmpty() && Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE).matcher(correoElectronico).matches()) {
            //correcto

        }else{
            camposValidos = false;
            avisoTextView.setVisibility(View.VISIBLE);
            avisoTextView.setText("El correo electrónico no posee un formato correcto.");
        }

        if (correoElectronico.isEmpty() || contrasena.isEmpty()  || repetirContrasena.isEmpty() || nombre.isEmpty() || apellidos.isEmpty() || direccion.isEmpty()) {
            camposValidos = false;
            avisoTextView.setVisibility(View.VISIBLE);
            avisoTextView.setText("Rellene todos los campos antes de continuar.");
        }

        return camposValidos;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == -1 && requestCode == 101){
            data.getExtras().get("data");
            String resultadoRecorte = String.valueOf(data.getExtras().get("data"));
            Uri uriResultadoRecorte = null;

            if (resultadoRecorte != null){
                uriResultadoRecorte = Uri.parse(resultadoRecorte);

                if (target == F_PERFIL){
                    perfil = uriResultadoRecorte;
                    imagenPerfil.setImageURI(perfil);
                }
            }
        }

    }
}