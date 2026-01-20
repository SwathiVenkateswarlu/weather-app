import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        Label title = new Label("Weather App");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        TextField cityField = new TextField();
        cityField.setPromptText("Enter city name");

        // 🔵 BLUE BUTTON
        Button getWeatherBtn = new Button("Get Weather");
        getWeatherBtn.setStyle(
                "-fx-background-color: #2196F3;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 8 24;" +
                        "-fx-background-radius: 8;"
        );

        ImageView weatherIcon = new ImageView();
        weatherIcon.setFitWidth(150);
        weatherIcon.setFitHeight(150);
        weatherIcon.setPreserveRatio(true);

        Label tempLabel = new Label("Temperature: --");
        Label humidityLabel = new Label("Humidity: --");
        Label conditionLabel = new Label("");

        getWeatherBtn.setOnAction(e -> {
            String city = cityField.getText().trim();
            if (city.isEmpty()) return;

            Task<Weather> task = new Task<>() {
                @Override
                protected Weather call() throws Exception {
                    return WeatherService.getWeather(city);
                }
            };

            task.setOnSucceeded(ev -> {
                Weather w = task.getValue();
                tempLabel.setText("Temperature: " + w.getTemperature() + " °C");
                humidityLabel.setText("Humidity: " + w.getHumidity() + " %");
                conditionLabel.setText("Condition: " + w.getCondition());
                conditionLabel.setStyle("-fx-text-fill: black;");

                weatherIcon.setImage(new Image(
                        getClass().getResourceAsStream("/images/" + w.getIcon())
                ));
            });

            task.setOnFailed(ev -> {
                tempLabel.setText("Temperature: --");
                humidityLabel.setText("Humidity: --");
                conditionLabel.setText("Condition: Invalid city or network error");
                conditionLabel.setStyle("-fx-text-fill: red;");

                weatherIcon.setImage(new Image(
                        getClass().getResourceAsStream("/images/unknown.png")
                ));
            });

            new Thread(task).start();
        });

        VBox root = new VBox(12,
                title,
                cityField,
                getWeatherBtn,
                weatherIcon,
                tempLabel,
                humidityLabel,
                conditionLabel
        );

        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        root.setStyle(
                "-fx-background-color: linear-gradient(to bottom,  #BBDEFB, #FFFFFF);"
        );

        stage.setScene(new Scene(root, 420, 560));
        stage.setTitle("Weather App");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
