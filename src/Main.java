import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
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

        TextField cityField = new TextField();
        cityField.setPromptText("Enter city name");

        Button getWeatherBtn = new Button("Get Weather");

        Label iconLabel = new Label();
        Label tempLabel = new Label("Temperature: --");
        Label humidityLabel = new Label("Humidity: --");
        Label conditionLabel = new Label("Condition:");

        // Load images (from src/images/)
        Image sunImg = new Image(getClass().getResourceAsStream("/images/sun.png"));
        Image cloudImg = new Image(getClass().getResourceAsStream("/images/cloud.png"));
        Image rainImg = new Image(getClass().getResourceAsStream("/images/rain.png"));
        Image snowImg = new Image(getClass().getResourceAsStream("/images/snow.png"));
        Image stormImg = new Image(getClass().getResourceAsStream("/images/strom.png"));
        Image unknownImg = new Image(getClass().getResourceAsStream("/images/unknown.png"));

        getWeatherBtn.setOnAction(e -> {

            String city = cityField.getText();

            if (city == null || city.trim().isEmpty()) {
                conditionLabel.setText("Condition: Please enter city name");
                conditionLabel.setStyle("-fx-text-fill: red;");
                iconLabel.setGraphic(new ImageView(unknownImg));
                return;
            }

            conditionLabel.setText("Condition: Loading...");
            conditionLabel.setStyle("-fx-text-fill: black;");
            getWeatherBtn.setDisable(true);

            Task<WeatherResponse> task = new Task<>() {
                @Override
                protected WeatherResponse call() {
                    return WeatherService.getWeather(city.trim());
                }
            };

            task.setOnSucceeded(event -> {
                getWeatherBtn.setDisable(false);

                WeatherResponse data = task.getValue();

                if (data == null || data.main == null || data.weather == null) {
                    tempLabel.setText("Temperature: --");
                    humidityLabel.setText("Humidity: --");
                    conditionLabel.setText("Condition: Invalid city or network error");
                    conditionLabel.setStyle("-fx-text-fill: red;");
                    iconLabel.setGraphic(new ImageView(unknownImg));
                    return;
                }

                tempLabel.setText("Temperature: " + data.main.temp + "°C");
                humidityLabel.setText("Humidity: " + data.main.humidity + "%");

                String desc = data.weather[0].description.toLowerCase();
                conditionLabel.setText("Condition: " + desc);
                conditionLabel.setStyle("-fx-text-fill: green;");

                ImageView iconView;

                if (desc.contains("clear")) {
                    iconView = new ImageView(sunImg);
                } else if (desc.contains("cloud")) {
                    iconView = new ImageView(cloudImg);
                } else if (desc.contains("rain")) {
                    iconView = new ImageView(rainImg);
                } else if (desc.contains("snow")) {
                    iconView = new ImageView(snowImg);
                } else if (desc.contains("storm") || desc.contains("thunder")) {
                    iconView = new ImageView(stormImg);
                } else if (
                        desc.contains("haze") ||
                                desc.contains("mist") ||
                                desc.contains("fog") ||
                                desc.contains("smoke")
                ) {
                    iconView = new ImageView(cloudImg);
                } else {
                    iconView = new ImageView(unknownImg);
                }

                iconView.setFitWidth(80);
                iconView.setFitHeight(80);
                iconLabel.setGraphic(iconView);
            });

            task.setOnFailed(event -> {
                getWeatherBtn.setDisable(false);
                tempLabel.setText("Temperature: --");
                humidityLabel.setText("Humidity: --");
                conditionLabel.setText("Condition: Network error");
                conditionLabel.setStyle("-fx-text-fill: red;");
                iconLabel.setGraphic(new ImageView(unknownImg));
            });

            new Thread(task).start();
        });

        VBox layout = new VBox(12,
                cityField,
                getWeatherBtn,
                iconLabel,
                tempLabel,
                humidityLabel,
                conditionLabel
        );

        layout.setPadding(new Insets(20));

        stage.setTitle("Weather App");
        stage.setScene(new Scene(layout, 350, 350));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}