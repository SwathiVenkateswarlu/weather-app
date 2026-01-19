import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherService {

    private static final String API_KEY = "a1b11f00a5081a14a4b289261a62b1a8";

    public static WeatherResponse getWeather(String city) {
        try {
            String urlStr =
                    "https://api.openweathermap.org/data/2.5/weather?q="
                            + city + "&units=metric&appid=" + API_KEY;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                return null;   // invalid city
            }

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            reader.close();

            return new Gson().fromJson(json.toString(), WeatherResponse.class);

        } catch (Exception e) {
            return null;
        }
    }
}
