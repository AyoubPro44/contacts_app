package boulahri.app.dal;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    private static Connection cnx = null;

    public static Connection getConnection() {
        try {
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            if(cnx == null)
                cnx = DriverManager.getConnection("jdbc:mysql://localhost:3306/contacts", "root", "");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cnx;
    }
}
