import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

public class Main {
    public static void main(String[] args) throws Exception {
        String apiKey = "test-creds@2320";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.cuvora.com/car/partner/cricket-data"))
                .header("apiKey", apiKey)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();

        System.out.println(responseBody);

        JSONObject jsonResponse = new JSONObject(responseBody);
        JSONArray matches = jsonResponse.optJSONArray("matches");

        if (matches == null) {
            System.out.println("No 'matches' array found in the expected location.");
            return;
        }

        int highestScore = 0;
        String highestScoringTeam = "";
        int countMatches300Plus = 0;

        for (int i = 0; i < matches.length(); i++) {
            JSONObject match = matches.getJSONObject(i);
            String t1 = match.getString("t1");
            String t2 = match.getString("t2");
            String t1s = match.getString("t1s");
            String t2s = match.getString("t2s");

            int t1Score = parseScore(t1s);
            int t2Score = parseScore(t2s);

            if (t1Score > highestScore) {
                highestScore = t1Score;
                highestScoringTeam = t1;
            }
            if (t2Score > highestScore) {
                highestScore = t2Score;
                highestScoringTeam = t2;
            }

            if (t1Score + t2Score > 300) {
                countMatches300Plus++;
            }
        }

        System.out.println("Highest Score in one innings: " + highestScore + " by " + highestScoringTeam);
        System.out.println("Number of matches with total score 300+: " + countMatches300Plus);

        String message = "Processed " + matches.length() + " matches";
        System.out.println(message);
    }

    private static int parseScore(String scoreStr) {
        if (scoreStr.isEmpty() || !scoreStr.contains("/")) {
            return 0;
        }
        try {
            return Integer.parseInt(scoreStr.split("/")[0]);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
