package com.example.garajeando;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferencias {

    private static final String NOMBRE_PREFERENCIAS = "Tema";
    private static final String KEY_TEMA_OSCURO= "esTemaOscuro";

    public static void setTemaOscuro(Context context, boolean isDark) {
        SharedPreferences preferences = context.getSharedPreferences(NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_TEMA_OSCURO, isDark);
        editor.apply();
    }

    public static boolean esTemaOscuro(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
        return preferences.getBoolean(KEY_TEMA_OSCURO, false);
    }

    public static void aplicarTema(Context context) {
        if (esTemaOscuro(context)) {
            context.setTheme(R.style.Noche_Theme_Garajeando);
        } else {
            context.setTheme(R.style.Dia_Theme_Garajeando);
        }
    }
}
