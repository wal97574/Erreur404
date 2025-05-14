package Services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ChatBot {

    private static final String API_KEY = "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";
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String MODEL = "mistralai/mistral-7b-instruct";

    public static String sendMessage(String userMessage) {
        try {
            if (userMessage.isEmpty()) return "Veuillez saisir votre message.";
            String jsonBody = "{"
                    + "\"model\": \"" + MODEL + "\","
                    + "\"messages\": [{\"role\": \"user\", \"content\": \"" + escape(userMessage) + "\"}]"
                    + "}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();
            // crude string parsing to get assistant's message content
            int index = responseBody.indexOf("\"content\":\"");
            if (index == -1) return "Erreur: contenu non trouvé";
            int start = index + 11;
            int end = responseBody.indexOf("\"", start);
            if (end == -1) return "Erreur: contenu mal formé";
            String reply = responseBody.substring(start, end);
            return reply.replace("\\n", "\n").replace("\\\"", "\"");

        } catch (Exception e) {
            return "Erreur: " + e.getMessage();
        }
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
