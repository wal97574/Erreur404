package tn.esprit.service;

import tn.esprit.entity.Emploi;
import tn.esprit.interfaces.IEmploiService;
import tn.esprit.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmploiService implements IEmploiService {
    private Connection conn = DatabaseConnection.getConnection();
    private final GoogleCalendarService googleCalendarService;

    public EmploiService(GoogleCalendarService googleCalendarService) {
        this.googleCalendarService = googleCalendarService;
    }

    @Override
    public void addEmploi(Emploi emploi) {
        String sql = "INSERT INTO emploi(cours_id, user_id, start_time, end_time, status, calendar_event_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, emploi.getCoursId());
            stmt.setInt(2, emploi.getUserId());
            stmt.setDate(3, Date.valueOf(emploi.getStartTime()));
            stmt.setDate(4, Date.valueOf(emploi.getEndTime()));
            stmt.setString(5, emploi.getStatus());

            // Add event to Google Calendar
            String eventId = googleCalendarService.addClassToCalendar(emploi);
            stmt.setString(6, eventId);

            stmt.executeUpdate();

            // Retrieve generated ID and set it to the emploi object
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    emploi.setId(generatedKeys.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateEmploi(Emploi emploi) {
        String sql = "UPDATE emploi SET cours_id=?, user_id=?, start_time=?, end_time=?, status=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, emploi.getCoursId());
            stmt.setInt(2, emploi.getUserId());
            stmt.setDate(3, Date.valueOf(emploi.getStartTime()));
            stmt.setDate(4, Date.valueOf(emploi.getEndTime()));
            stmt.setString(5, emploi.getStatus());
            stmt.setInt(6, emploi.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteEmploi(int id) {
        String selectSql = "SELECT calendar_event_id FROM emploi WHERE id=?";
        try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setInt(1, id);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                String eventId = rs.getString("calendar_event_id");

                // Delete event from Google Calendar
                if (eventId != null) {
                    googleCalendarService.cancelCalendarEvent(eventId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String deleteSql = "DELETE FROM emploi WHERE id=?";
        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
            deleteStmt.setInt(1, id);
            deleteStmt.executeUpdate();
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
                        rs.getDate("start_time").toLocalDate(),
                        rs.getDate("end_time").toLocalDate(),
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
                        rs.getDate("start_time").toLocalDate(),
                        rs.getDate("end_time").toLocalDate(),
                        rs.getString("status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
