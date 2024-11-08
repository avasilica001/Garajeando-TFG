package com.example.garajeando;

import android.app.Application;

public class MiAplicacion extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Preferencias.aplicarTema(getApplicationContext());
    }
}
