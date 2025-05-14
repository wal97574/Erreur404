package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {
    final String URl="jdbc:mysql://localhost:3306/gym_management";
    final String USERNAME="root";
    final String PASSWORD="";
    Connection connection;

    static DataSource instance;
    private DataSource(){
        try {
            connection= DriverManager.getConnection(URl,USERNAME,PASSWORD);
            System.out.println("Connected to database");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    public  static DataSource getInstance(){
        if(instance==null)
            instance=new DataSource();
        return instance;
    }
    public Connection getConnection() {
        return connection;
    }
}
