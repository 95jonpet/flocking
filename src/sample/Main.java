package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Flocking Simulator");
        primaryStage.setScene(new Scene(root, 752, 620));
        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> Platform.exit());

        new Controller(root);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
