
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ScoreSender {

    private static final String API_URL = "http://localhost:8080/api/scores";
    // Create the client once here so it's reused
    private static final HttpClient client = HttpClient.newHttpClient();

    public static void sendScore(String playerName, long score) {
        try {
            String json = String.format("{\"playerName\": \"%s\", \"score\": %d}", playerName, score);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            // Notice we use the 'client' we defined above
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> System.out.println("Score Sent! Status: " + response.statusCode()))
                    .exceptionally(ex -> {
                        System.err.println("Failed to send score: " + ex.getMessage());
                        return null;
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CompletableFuture<List<String>> getTopScores() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .GET()
                .build();

        // We return the "Future" so the game can wait for it
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    List<String> formattedScores = new ArrayList<>();
                    String body = response.body();

                    try {
                        // If database is empty, return empty list
                        if (body.equals("[]")) {
                            return formattedScores;
                        }

                        // Manual parsing (The "No-Library" special)
                        String clean = body.replace("[", "").replace("]", "").replace("{", "").replace("\"", "");
                        String[] entries = clean.split("},");

                        for (String entry : entries) {
                            String[] parts = entry.replace("}", "").split(",");
                            // These indexes assume your API sends: id, createdAt, playerName, score
                            String name = parts[2].split(":")[1];
                            String score = parts[3].split(":")[1];
                            formattedScores.add(name + ": " + score);
                        }
                    } catch (Exception e) {
                        System.err.println("Parsing error: " + e.getMessage());
                    }
                    return formattedScores;
                });
    }
}
