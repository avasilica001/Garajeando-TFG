package com.example.garajeando;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class AdministradorPeticiones {
    private static AdministradorPeticiones miAdministradorPeticiones;
    private  RequestQueue peticionCola;
    private static Context contexto;

    private AdministradorPeticiones(Context context) {
        contexto = context;
        peticionCola = getRequestQueue();
    }

    public static synchronized AdministradorPeticiones getInstance(Context context) {
        if (miAdministradorPeticiones == null) {
            miAdministradorPeticiones = new AdministradorPeticiones(context);
        }
        return miAdministradorPeticiones;
    }

    public RequestQueue getRequestQueue() {
        if (peticionCola == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            peticionCola = Volley.newRequestQueue(contexto.getApplicationContext());
        }
        return peticionCola;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public void cancelAll(String peticion) {
        peticionCola.cancelAll(peticion);
    }

    public interface VolleyCallBack {
        void onSuccess();
    }
}