package tn.esprit.service;

import tn.esprit.entity.Emploi;
import tn.esprit.interfaces.IEmploiService;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import tn.esprit.utils.DatabaseConnection;

public class EmploiService implements IEmploiService {
    private Connection conn = DatabaseConnection.getConnection();

    @Override
    public void addEmploi(Emploi emploi) {
        String sql = "INSERT INTO emploi(cours_id, user_id, start_time, end_time, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, emploi.getCoursId());
            stmt.setInt(2, emploi.getUserId());
            stmt.setDate(3, Date.valueOf(emploi.getStartTime())); // Use Date.valueOf for LocalDate
            stmt.setDate(4, Date.valueOf(emploi.getEndTime()));   // Use Date.valueOf for LocalDate
            stmt.setString(5, emploi.getStatus());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateEmploi(Emploi emploi) {
        String sql = "UPDATE emploi SET cours_id=?, user_id=?, start_time=?, end_time=?, status=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, emploi.getCoursId());
            stmt.setInt(2, emploi.getUserId());
            stmt.setDate(3, Date.valueOf(emploi.getStartTime())); // Use Date.valueOf for LocalDate
            stmt.setDate(4, Date.valueOf(emploi.getEndTime()));   // Use Date.valueOf for LocalDate
            stmt.setString(5, emploi.getStatus());
            stmt.setInt(6, emploi.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteEmploi(int id) {
        String sql = "DELETE FROM emploi WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Emploi> getAllEmplois() {
        List<Emploi> list = new ArrayList<>();
        String sql = "SELECT * FROM emploi";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Emploi(
                        rs.getInt("id"),
                        rs.getInt("cours_id"),
                        rs.getInt("user_id"),
                        rs.getDate("start_time").toLocalDate(), // Convert Date to LocalDate
                        rs.getDate("end_time").toLocalDate(),   // Convert Date to LocalDate
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Emploi getEmploiById(int id) {
        String sql = "SELECT * FROM emploi WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Emploi(
                        rs.getInt("id"),
                        rs.getInt("cours_id"),
                        rs.getInt("user_id"),
                        rs.getDate("start_time").toLocalDate(), // Convert Date to LocalDate
                        rs.getDate("end_time").toLocalDate(),   // Convert Date to LocalDate
                        rs.getString("status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
