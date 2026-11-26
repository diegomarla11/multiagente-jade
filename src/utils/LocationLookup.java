package utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public final class LocationLookup {
    private LocationLookup() {}

    @SuppressWarnings("unchecked")
    public static String forPostalCode(String postalCode) {
        if (!"1".equals(System.getenv("JADE_ENABLE_OPENAI"))) {
            return "Simulated resource: Centro Demo (postal code " + postalCode + ").";
        }
        String key = System.getenv("OPENAI_API_KEY");
        String model = System.getenv("OPENAI_MODEL");
        if (key == null || key.isBlank() || model == null || model.isBlank()) {
            return "Online lookup unavailable: configure OPENAI_API_KEY and OPENAI_MODEL.";
        }

        JSONObject prompt = new JSONObject();
        prompt.put("role", "user");
        prompt.put("content", "For an academic software simulation, invent one clearly fictional "
            + "Spanish hospital name for sample postal code " + postalCode + ". Return only the name.");
        JSONArray messages = new JSONArray();
        messages.add(prompt);
        JSONObject body = new JSONObject();
        body.put("model", model);
        body.put("messages", messages);
        try {
            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
            HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.openai.com/v1/chat/completions"))
                .timeout(Duration.ofSeconds(15))
                .header("Authorization", "Bearer " + key)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toJSONString())).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return "Online lookup unavailable (HTTP " + response.statusCode() + ").";
            JSONObject data = (JSONObject) new JSONParser().parse(response.body());
            JSONArray choices = (JSONArray) data.get("choices");
            JSONObject choice = (JSONObject) choices.get(0);
            JSONObject message = (JSONObject) choice.get("message");
            String name = (String) message.get("content");
            if (name == null || name.isBlank()) return "Online lookup returned no name.";
            return "AI-generated fictional resource: " + name.strip();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Online lookup interrupted.";
        } catch (IOException | ParseException | RuntimeException e) {
            return "Online lookup unavailable.";
        }
    }
}
