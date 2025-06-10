import java.io.*;
import java.net.*;
import java.util.Scanner;

public class WeatherApp {
    private static final String BASE_URL = "http://api.weatherapi.com/v1/current.json";

    public static void main(String[] args) {
        String apiKey = getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("❌ API key missing. Make sure it's in the .env file.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter city name: ");
        String city = scanner.nextLine().trim();

        try {
            String urlStr = BASE_URL + "?key=" + apiKey + "&q=" + city;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int status = conn.getResponseCode();
            if (status != 200) {
                System.out.println("❌ Error: Failed to get weather data.");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder json = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            reader.close();

            String response = json.toString();
            String temp = extract(response, "\"temp_c\":", ",");
            String condition = extract(response, "\"text\":\"", "\"");
            String humidity = extract(response, "\"humidity\":", ",");

            System.out.println("\n🌤️ Weather in " + city + ":");
            System.out.println("Temperature: " + temp + "°C");
            System.out.println("Condition: " + condition);
            System.out.println("Humidity: " + humidity + "%");

        } catch (Exception e) {
            System.out.println("⚠️ Something went wrong: " + e.getMessage());
        }
    }

    private static String getApiKey() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(".env"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("API_KEY=")) {
                    return line.split("=", 2)[1].trim();
                }
            }
        } catch (IOException e) {
            System.out.println("⚠️ Could not read .env file.");
        }
        return null;
    }

    private static String extract(String source, String start, String end) {
        try {
            int startIndex = source.indexOf(start) + start.length();
            int endIndex = source.indexOf(end, startIndex);
            return source.substring(startIndex, endIndex);
        } catch (Exception e) {
            return "N/A";
        }
    }
}
