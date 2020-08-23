import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        getBowlingData();
        // postBowlingData();
        testCalculater();
    }

    private static void getBowlingData() {
        try {
            HttpClient client = HttpClient.newHttpClient();            
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://13.74.31.101/api/points")).build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body)
                    .thenApply(App::parse).join();
       
        } catch (Exception e) {
            System.err.println("HTTP exception: " + e);
        }
    }

    private static void postBowlingData() throws IOException, InterruptedException {

        //Send json body, in format:
        // {"token": "124jgjfj3FkenI", "points": [5, 10, 25, 30]}
        //Use data from parse method

        // HttpClient client = HttpClient.newHttpClient();
        // HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://13.74.31.101/api/points"))
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

    public static void testCalculater(){
        int[][] testPoints1 = { {5, 4}, {1,3}, {2,1} };
        var testData1 = calculate(testPoints1);
        int[] testExpectedPoints1 = new int[] { 9, 13, 16 };
    
        // comparing testPoints1 and testExpectedPoints1
        boolean isCalculatedCorrect1 = Arrays.equals(testData1, testExpectedPoints1);
        System.out.println("testcase 1:  testPoints1 and expectedPoints1 is equal? " + isCalculatedCorrect1);
    

        int[][] testPoints2 = { {10,0}, {10,0}, {0,10}, {10,0}, {10,0}, {10,0}, {0,0} };
        var testData2 = calculate(testPoints2);
        int[] testExpectedPoints2 = new int[]{20, 40, 60, 90, 110, 120, 120 };

        // comparing testPoints2 and testExpectedPoints2
        boolean isCalculatedCorrect2 = Arrays.equals(testData2, testExpectedPoints2);
        System.out.println("testcase 2:  testPoints2 and expectedPoints2 is equal? " + isCalculatedCorrect2);

    }
}