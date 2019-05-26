package com.example.appetito;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Callable;


public class DBData implements Callable<String> {
    private Connection connection;
    private String query;

    public DBData(String query){
        this.query = query;
    }

    public String call() throws Exception{
        try {

            Class.forName("org.postgresql.Driver");

        } catch (ClassNotFoundException e) {

            System.out.println("Where is your PostgreSQL JDBC Driver? "
                    + "Include in your library path!");
            e.printStackTrace();
            return "";

        }

        System.out.println("PostgreSQL JDBC Driver Registered!");

        try {

            connection = DriverManager.getConnection(
                    "jdbc:postgresql://35.232.156.93:5432/gastrogt", "postgres",
                    "A12345678@");

        } catch (SQLException e) {

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return "";

        }

        if (connection != null) {
            System.out.println("You made it, take control your database now!");
        } else {
            System.out.println("Failed to make connection!");
        }

        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);

            if (rs.next()) {
                System.out.println(rs.getString(1)+" "+rs.getString(2));
                return rs.getString(2);
            }
        }catch (SQLException e){

        }
        return "";

    }

}
