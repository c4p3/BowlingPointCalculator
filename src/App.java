import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;


public class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        getBowlingData();
        // postBowlingData();
    }

    private static void getBowlingData() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            //kan jeg ikke bruge dette?
            
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://13.74.31.101/api/points")).build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body)
                    .thenApply(App::parse).join();
       
        } catch (Exception e) {
            System.err.println("HTTP exception: " + e);
        }
    }

    private static void postBowlingData() throws IOException, InterruptedException {
        String json = "{\"token\":\"tokentokeno\", \"points\": [5, 10, 25, 30]}";

        JSONObject obj = new JSONObject(json);
        JSONArray points = obj.getJSONArray("points");

        for(int i = 0; points.length() < 1; i++)
        {
            System.out.println(i);
        }
        obj.put("points", points);

        System.out.println(obj);

        var objectMapper = new ObjectMapper();

        // Map<String,Object> params = new LinkedHashMap<>();
        // params.put("token", "blablabla");
        // System.out.println(obj);


        String requestBody = objectMapper.writeValueAsString(obj);
        // System.out.println(requestBody);

        // HttpClient client = HttpClient.newHttpClient();
        // HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://httpbin.org/post"))
        //         .POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();

        // HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // System.out.println(response.body());

    }

    // Parse data to int array
    public static String parse(String responseBody) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            BowlingData bowlingData = mapper.readValue(responseBody, BowlingData.class);
            int[][] points = bowlingData.getPoints();
            calculate(points);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println("parse: " + responseBody);
        return responseBody;
    }

    public static int[] calculate(int[][] points) {
        int[] framePoints = new int[points.length];

        int total = 0;
        for (int i = 0; i < points.length; i++) {
            total += points[i][0];
            total += points[i][1];

            try {
                // Strike
                if (points[i][0] == 10) {
                    total += points[i + 1][0]; // adds next throw
                    if (points[i + 1][0] == 10) {
                        total += points[i + 2][0]; // adds second throw, after strike
                    } else {
                        total += points[i + 1][1];
                    }
                    // Spare
                } else if (points[i][0] + points[i][1] == 10) {
                    total += points[i + 1][0];
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                // ... End of play
            } finally {
                framePoints[i] = total;
            }
        }
        return framePoints;
    }
}