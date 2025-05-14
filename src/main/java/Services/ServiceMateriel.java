package Services;

import java.sql.*;

import Entities.Materiel;
import Utils.DataSource;
import java.sql.Connection;
import java.util.ArrayList;

public class ServiceMateriel {

    protected Connection conn=DataSource.getInstance().getConnection();

    private PreparedStatement insertStmt;
    private PreparedStatement selectStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement deleteStmt;

    public ServiceMateriel() {
        try {
            insertStmt = conn.prepareStatement("insert into materiel(nom,date_maintenance) values (?,?)", Statement.RETURN_GENERATED_KEYS);
            selectStmt = conn.prepareStatement("select * from materiel where id=?");
            updateStmt = conn.prepareStatement("update materiel set nom=?,date_maintenance=? where id=?");
            deleteStmt = conn.prepareStatement("delete from materiel where id=?");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public boolean addMateriel(Materiel materiel){
        try {
            insertStmt.setString(1,materiel.getNom());
            java.sql.Date sqlDate = new java.sql.Date(materiel.getDateMaintenance().getTime());
            insertStmt.setDate(2, sqlDate);
            if(insertStmt.executeUpdate()>0){
                ResultSet rs=insertStmt.getGeneratedKeys();
                if(rs.next()){
                    materiel.setId(rs.getInt(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
    public Materiel getMateriel(int id){
        try {
            selectStmt.setInt(1, id);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                java.sql.Date sqlDate = rs.getDate("date_maintenance");
                java.util.Date dateMaintenance = new java.util.Date(sqlDate.getTime());

                return new Materiel(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        dateMaintenance
                );
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }
    public boolean updateMateriel(Materiel materiel){
        try {
            java.sql.Date sqlDate = new java.sql.Date(materiel.getDateMaintenance().getTime());
            updateStmt.setString(1,materiel.getNom());
            updateStmt.setDate(2,sqlDate);
            updateStmt.setInt(3,materiel.getId());
            if(updateStmt.executeUpdate()>0)
                return true;
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
    public boolean deleteMateriel(int id){
        try {
            deleteStmt.setInt(1,id);
            if(deleteStmt.executeUpdate()>0)
                return true;
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
    public ArrayList<Materiel> getAllMateriel(){
        ArrayList<Materiel> salleList = new ArrayList<>();

        String sql = "SELECT * FROM materiel";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                java.sql.Date sqlDate = rs.getDate("date_maintenance");
                java.util.Date dateMaintenance = new java.util.Date(sqlDate.getTime());
                salleList.add(new Materiel(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        dateMaintenance
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching salles: " + e.getMessage());
        }

        return salleList;
    }
}
