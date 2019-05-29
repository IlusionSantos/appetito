package com.example.appetito;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
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
    private TextView nombrePlatillo;
    private TextView restaurante;
    private TextView receta;
    public String platillos="";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_layout);
        platillos =getIntent().getExtras().getString("platillo");
        platillo = (TextView) findViewById(R.id.infoplatillo);
        nombrePlatillo = (TextView) findViewById(R.id.nombreplatillo);
        receta = (TextView)findViewById(R.id.inforeceta);
        restaurante = (TextView) findViewById(R.id.inforestaurantes);
        nombrePlatillo.setText("Platillo: "+platillos);
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
                final ResultSet rs = st.executeQuery("SELECT preparacion, descripcion, calorias FROM platillos WHERE nombre_platillo='"+platillos+"'");
                String preparacion = "";
                if (rs.next()) {
                    preparacion = rs.getString(1);
                    final String descr = rs.getString(2);
                    final String cal = rs.getString(3);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String msg = getString(R.string.descripcion)+"\n\n"+descr+"\n\n"+getString(R.string.calorias)+"\n\n"+cal;
                            platillo.setText(msg);
                        }
                    });
                    Log.v("DATABASE",rs.getString(1)+" "+rs.getString(2));
                }

                ResultSet rs2 = st.executeQuery("SELECT I.nombre_ingrediente, RPI.cantidad FROM platillos P, ingredientes I, relacion_platillos_ingredientes RPI WHERE P.id_platillo = RPI.id_platillo AND I.id_ingrediente = RPI.id_ingrediente AND P.nombre_platillo = '"+platillos+"'");
                String data = "";
                while(rs2.next()){
                    data+= "- "+rs2.getString(1)+" "+rs2.getString(2)+"\n";
                }
                final String msg = data;
                final String msgpre = preparacion;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String text = getString(R.string.ingredientes)+"\n\n"+ msg+"\n"+getString(R.string.preparacion)+"\n\n"+msgpre;
                        receta.setText(text);
                    }
                });

                rs2 = st.executeQuery("SELECT R.nombre_restaurante,R.direccion,RPR.precio FROM platillos P, restaurantes R, relacion_platillos_restaurantes RPR WHERE P.id_platillo = RPR.id_platillo AND R.id_restaurante = RPR.id_restaurante AND P.nombre_platillo = '"+platillos+"'");
                data="";
                while(rs2.next()){
                    data+= "- "+rs2.getString(1)+"\n  Dirección: "+rs2.getString(2)+"\n  Precio: Q"+rs2.getString(3)+"\n\n";
                }
                final String res = data;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String text = getString(R.string.restaurantes)+"\n\n"+res;
                        restaurante.setText(text);
                    }
                });
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
