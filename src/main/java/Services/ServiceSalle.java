package Services;

import java.sql.*;

import Entities.Salle;
import Utils.DataSource;
import java.sql.Connection;
import java.util.ArrayList;


public class ServiceSalle {

    protected Connection conn=DataSource.getInstance().getConnection();

    private PreparedStatement insertStmt;
    private PreparedStatement selectStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement deleteStmt;

    public ServiceSalle() {
        try {
            insertStmt = conn.prepareStatement("insert into salle(nom,nbr_max) values (?,?)", Statement.RETURN_GENERATED_KEYS);
            selectStmt = conn.prepareStatement("select * from salle where id=?");
            updateStmt = conn.prepareStatement("update salle set nom=?,nbr_max=? where id=?");
            deleteStmt = conn.prepareStatement("delete from salle where id=?");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
    public boolean addSalle(Salle salle){
        try {
            insertStmt.setString(1,salle.getNom());
            insertStmt.setInt(2,salle.getNbrMax());
            if(insertStmt.executeUpdate()>0){
                ResultSet rs=insertStmt.getGeneratedKeys();
                if(rs.next()){
                    salle.setId(rs.getInt(1));
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
        return false;
    }
    public Salle getSalle(int id){
        try {
            selectStmt.setInt(1,id);
            ResultSet rs=selectStmt.executeQuery();
            if(rs.next()){
                Salle salle=new Salle();
                salle.setId(rs.getInt("id"));
                salle.setNom(rs.getString("nom"));
                salle.setNbrMax(rs.getInt("nbr_max"));
                return salle;
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }
    public boolean updateSalle(Salle salle){
        try {
            updateStmt.setString(1,salle.getNom());
            updateStmt.setInt(2,salle.getNbrMax());
            updateStmt.setInt(3,salle.getId());
            if(updateStmt.executeUpdate()>0)
                return true;
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
    public boolean deleteSalle(int id){
        try {
            deleteStmt.setInt(1,id);
            if(deleteStmt.executeUpdate()>0)
                return true;
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
    public ArrayList<Salle> getAllSalle(){
        ArrayList<Salle> materielList = new ArrayList<>();

        String sql = "SELECT * FROM salle";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                materielList.add(new Salle(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getInt("nbr_max")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching salles: " + e.getMessage());
        }

        return materielList;
    }
}
