package tn.esprit.entity;

public class Cours {
    private int id;
    private String description;
    private int trainerId;
    private int maxParticipants;
    private int durationMinutes;

    // Constructors
    public Cours() {}

    public Cours(int id, String description, int trainerId, int maxParticipants, int durationMinutes) {
        this.id = id;
        this.description = description;
        this.trainerId = trainerId;
        this.maxParticipants = maxParticipants;
        this.durationMinutes = durationMinutes;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public int getTrainerId() {
        return trainerId;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
}
