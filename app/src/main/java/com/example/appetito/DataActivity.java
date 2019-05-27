package com.example.appetito;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        platillo.setText("Descripción");
        sqlThread.start();


    }

    Thread sqlThread = new Thread() {
        public void run() {
            //DriverManager.getConnection("jdbc:postgresql://35.232.156.93 :5432/gastrogt", "postgres", "A12345678@");
            try {

                Class.forName("org.postgresql.Driver");

            } catch (ClassNotFoundException e) {

                System.out.println("Where is your PostgreSQL JDBC Driver? "
                        + "Include in your library path!");
                e.printStackTrace();
                return;

            }

            System.out.println("PostgreSQL JDBC Driver Registered!");

            Connection connection = null;

            try {

                connection = DriverManager.getConnection(
                        "jdbc:postgresql://35.232.156.93:5432/gastrogt", "postgres",
                        "A12345678@");

            } catch (SQLException e) {

                System.out.println("Connection Failed! Check output console");
                e.printStackTrace();
                return;

            }

            if (connection != null) {
                System.out.println("You made it, take control your database now!");
            } else {
                System.out.println("Failed to make connection!");
            }

            try {
                Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery("SELECT preparacion, descripcion, calorias FROM platillos WHERE nombre_platillo='Fiambre'");

                if (rs.next()) {
                    platillo.setText("Descripcion: "+rs.getString(2));
                    System.out.println(rs.getString(1)+" "+rs.getString(2));
                }
            }catch (SQLException e){

            }

        }
    };

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
