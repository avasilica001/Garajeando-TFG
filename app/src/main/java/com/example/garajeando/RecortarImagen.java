package com.example.garajeando;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.util.UnstableApi;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;

public class RecortarImagen extends AppCompatActivity {

    String resultado;
    String uri;
    String formatoImagen;
    int ejeX, ejeY;

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recortar_imagen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.crearComunidad), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Intent intent = getIntent();

        if(getIntent().hasExtra("data")){
            resultado = getIntent().getExtras().getString("data");
            uri = resultado;
            formatoImagen = getIntent().getExtras().getString("formato");

            if(formatoImagen.equals("169")){
                ejeX = 16;
                ejeY= 9;
            }else{
                ejeX = 1;
                ejeY= 1;
            }

            String uri_destino = UUID.randomUUID().toString() + ".png";

            UCrop.Options opciones = new UCrop.Options();

            UCrop.of(Uri.parse(uri), Uri.fromFile(new File(getCacheDir(), uri_destino)))
                    .withOptions(opciones)
                    .withAspectRatio(ejeX,ejeY)
                    .withMaxResultSize(1000,1000)
                    .start(RecortarImagen.this);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            final Uri resultadoFinal = UCrop.getOutput(data);
            Intent intentResultado = new Intent();
            intentResultado.putExtra("data",resultadoFinal);
            setResult(-1,intentResultado);
            finish();
    }
}