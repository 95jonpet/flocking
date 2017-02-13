package se.peterjonsson.flocking;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The starting point of the application.
 * Creates a new main view.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Flocking Simulator");
        primaryStage.setScene(new Scene(root, 752, 620));
        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> Platform.exit());
        primaryStage.requestFocus();

        new MainController(root);
    }


    /**
     * Called on application start.
     * This launches the application and calls {@link #start(Stage)}.
     * @param args Launch arguments. These are currently not used.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
