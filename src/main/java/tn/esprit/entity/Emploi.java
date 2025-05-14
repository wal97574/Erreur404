package tn.esprit.entity;

import java.time.LocalDate;

public class Emploi {
    private int id;
    private int coursId;
    private int userId;
    private LocalDate startTime; // Changed to LocalDate
    private LocalDate endTime;   // Changed to LocalDate
    private String status;

    // Constructors
    public Emploi() {}

    public Emploi(int id, int coursId, int userId, LocalDate startTime, LocalDate endTime, String status) {
        this.id = id;
        this.coursId = coursId;
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCoursId() {
        return coursId;
    }

    public void setCoursId(int coursId) {
        this.coursId = coursId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDate getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDate startTime) {
        this.startTime = startTime;
    }

    public LocalDate getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDate endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
