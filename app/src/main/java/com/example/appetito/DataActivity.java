package com.example.appetito;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DataActivity extends AppCompatActivity {
    private TextView platillo;
    private TextView restaurante;
    private TextView receta;
    public String platillos="";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_layout);
        platillos =getIntent().getExtras().getString("platillo");
        platillo = (TextView) findViewById(R.id.idnombreplatillo);

        try {
            ExecutorService servicio= Executors.newFixedThreadPool(1);
            Future<String> resultado= servicio.submit(new DBData("SELECT preparacion, descripcion, calorias FROM platillos WHERE nombre_platillo='"+platillos+"'"));
            if(resultado.isDone()) {
                platillo.setText("Descripción: "+resultado.get());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }

    public String getPlatillo(){
        try {
            ExecutorService servicio= Executors.newFixedThreadPool(1);
            Future<String> resultado= servicio.submit(new DBData("SELECT preparacion, descripcion, calorias FROM platillos WHERE nombre_platillo='"+platillos+"'"));
            if(resultado.isDone()) {
                System.out.println("RESULTADO-------"+resultado.get());
                return resultado.get();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getReceta(){
        return "";
    }

    public String getRestaurantes(){
        return "";
    }




    /*CODIGO PARA HACER CONSULTAS
    *
    *       try {
                    ExecutorService servicio= Executors.newFixedThreadPool(1);
                    Future<String> resultado= servicio.submit(new DBData("SELECT * FROM platillos"));
                    if(resultado.isDone()) {
                        System.out.println(resultado.get());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
    *
    * */


}
