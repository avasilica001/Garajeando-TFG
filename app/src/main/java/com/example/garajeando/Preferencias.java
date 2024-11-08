package com.example.garajeando;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;

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
            //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            context.setTheme(R.style.Noche_Theme_Garajeando);
        } else {
            //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            context.setTheme(R.style.Dia_Theme_Garajeando);
        }
    }

    public static void setTemaAlertDialogConOpciones(AlertDialog.Builder builder, Context context){
        AlertDialog dialogo = builder.create();

        int[] atributos = new int[]{R.attr.dialogBackgroundColor, R.attr.dialogTextColor};
        TypedArray typedArray = context.obtainStyledAttributes(atributos);
        int colorFondo = typedArray.getColor(0, 0);
        int colorTexto = typedArray.getColor(1, 0);
        typedArray.recycle();

        if (dialogo.getWindow() != null) {
            dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(colorFondo));
        }

        dialogo.show();

        TextView messageView = dialogo.findViewById(android.R.id.message);
        if (messageView != null) messageView.setTextColor(colorTexto);

        ListView listViewOpciones = dialogo.getListView();
        for (int i = 0; i < listViewOpciones.getChildCount(); i++) {
            TextView opcionTextView = (TextView) listViewOpciones.getChildAt(i);
            if (opcionTextView != null) {
                opcionTextView.setTextColor(colorTexto);
            }
        }

        listViewOpciones.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                if (child instanceof TextView) {
                    ((TextView) child).setTextColor(colorTexto);
                }
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {
                //no hace falta
            }
        });
    }

    public static void setTemaAlertDialogPositivoNegativo(AlertDialog.Builder builder, Context context){
        AlertDialog dialogo = builder.create();

        int[] atributos = new int[]{R.attr.dialogBackgroundColor, R.attr.dialogTextColor, R.attr.dialogButtonTextColor};
        TypedArray typedArray = context.obtainStyledAttributes(atributos);
        int colorFondo = typedArray.getColor(0, 0);
        int colorTexto = typedArray.getColor(1, 0);
        int colorBotonTexto = typedArray.getColor(2, 0);
        typedArray.recycle();

        if (dialogo.getWindow() != null) {
            dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(colorFondo));
        }

        dialogo.show();

        TextView titleView = dialogo.findViewById(android.R.id.title);
        TextView messageView = dialogo.findViewById(android.R.id.message);
        if (titleView != null) titleView.setTextColor(colorTexto);
        if (messageView != null) messageView.setTextColor(colorTexto);

        Button positiveButton = dialogo.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialogo.getButton(AlertDialog.BUTTON_NEGATIVE);
        if (positiveButton != null) positiveButton.setTextColor(colorBotonTexto);
        if (negativeButton != null) negativeButton.setTextColor(colorBotonTexto);
    }
}
