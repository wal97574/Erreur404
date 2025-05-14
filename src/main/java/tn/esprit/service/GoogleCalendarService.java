package tn.esprit.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import tn.esprit.entity.Cours;
import tn.esprit.entity.Emploi;
import tn.esprit.service.CoursService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

public class GoogleCalendarService {
    private static final String APPLICATION_NAME = "Gym Management System";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private final CoursService coursService;

    public GoogleCalendarService(CoursService coursService) {
        this.coursService = coursService;
    }

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = GoogleCalendarService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private Calendar getCalendarService() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public String addClassToCalendar(Emploi emploi) throws IOException, GeneralSecurityException {
        Cours cours = coursService.getCoursById(emploi.getCoursId());
        if (cours == null) {
            throw new IllegalArgumentException("No course found with ID: " + emploi.getCoursId());
        }

        Calendar service = getCalendarService();

        // Convert LocalDate to DateTime (set time to 10:00 AM as default)
        LocalDateTime startLDT = emploi.getStartTime().atTime(10, 0);
        LocalDateTime endLDT = emploi.getEndTime().atTime(11, 0); // Assuming 1 hour duration

        DateTime startDateTime = new DateTime(startLDT.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        DateTime endDateTime = new DateTime(endLDT.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

        Event event = new Event()
                .setSummary("Gym Class: " + cours.getDescription())
                .setDescription("Trainer ID: " + cours.getTrainerId() +
                        "\nMax Participants: " + cours.getMaxParticipants())
                .setStart(new EventDateTime().setDateTime(startDateTime))
                .setEnd(new EventDateTime().setDateTime(endDateTime));

        Event createdEvent = service.events().insert("primary", event).execute();
        return createdEvent.getId();
    }

    public void cancelCalendarEvent(String eventId) throws IOException, GeneralSecurityException {
        Calendar service = getCalendarService();
        service.events().delete("primary", eventId).execute();
    }
}
