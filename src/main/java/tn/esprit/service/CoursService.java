package tn.esprit.service;

// CoursService.java
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.properties.TextAlignment;
import tn.esprit.entity.Cours;
import tn.esprit.interfaces.ICoursService;
import tn.esprit.utils.DatabaseConnection;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.sql.*;
import java.util.*;

public class CoursService implements ICoursService {
    private Connection conn = DatabaseConnection.getConnection();

    @Override
    public void addCours(Cours cours) {
        String sql = "INSERT INTO cours(description, trainer_id, max_participants, duration_minutes) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cours.getDescription());
            stmt.setInt(2, cours.getTrainerId());
            stmt.setInt(3, cours.getMaxParticipants());
            stmt.setInt(4, cours.getDurationMinutes());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void updateCours(Cours cours) {
        String sql = "UPDATE cours SET description=?, trainer_id=?, max_participants=?, duration_minutes=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cours.getDescription());
            stmt.setInt(2, cours.getTrainerId());
            stmt.setInt(3, cours.getMaxParticipants());
            stmt.setInt(4, cours.getDurationMinutes());
            stmt.setInt(5, cours.getId());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void deleteCours(int id) {
        String sql = "DELETE FROM cours WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public List<Cours> getAllCours() {
        List<Cours> list = new ArrayList<>();
        String sql = "SELECT * FROM cours";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Cours(
                        rs.getInt("id"),
                        rs.getString("description"),
                        rs.getInt("trainer_id"),
                        rs.getInt("max_participants"),
                        rs.getInt("duration_minutes")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public Cours getCoursById(int id) {
        String sql = "SELECT * FROM cours WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Cours(
                        rs.getInt("id"),
                        rs.getString("description"),
                        rs.getInt("trainer_id"),
                        rs.getInt("max_participants"),
                        rs.getInt("duration_minutes")
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    public void generateCoursPdf(Cours cours, String filePath) {
        try (PdfWriter writer = new PdfWriter(filePath);
             Document document = new Document(new com.itextpdf.kernel.pdf.PdfDocument(writer))) {

            // Gym theme colors
            Color headerColor = new DeviceRgb(52, 152, 219); // Blue
            Color textColor = new DeviceRgb(44, 62, 80); // Dark gray

            // Add title
            Paragraph title = new Paragraph("Gym Class Details")
                    .setFontSize(20)
                    .setBold()
                    .setFontColor(headerColor)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            // Add course details
            document.add(new Paragraph("ID: " + cours.getId()).setFontColor(textColor));
            document.add(new Paragraph("Description: " + cours.getDescription()).setFontColor(textColor));
            document.add(new Paragraph("Trainer ID: " + cours.getTrainerId()).setFontColor(textColor));
            document.add(new Paragraph("Max Participants: " + cours.getMaxParticipants()).setFontColor(textColor));
            document.add(new Paragraph("Duration (Minutes): " + cours.getDurationMinutes()).setFontColor(textColor));

            // Add footer
            Paragraph footer = new Paragraph("Stay Fit, Stay Healthy!")
                    .setFontSize(12)
                    .setFontColor(headerColor)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(20);
            document.add(footer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
