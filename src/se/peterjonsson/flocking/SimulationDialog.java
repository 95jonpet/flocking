package se.peterjonsson.flocking;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * A dialog to show when running a simulation.
 * This will automatically run an attached simulation and show its progress.
 */
class SimulationDialog {
    /**
     * The dialog title to show.
     */
    private static final String TITLE = "Running flocking simulation";

    /**
     * The stage that the dialog belongs to.
     */
    private final Stage stage;

    /**
     * Attached simulation to run.
     */
    private final FlockingSimulation simulation;

    /**
     * Creates a new dialog containing a simulation.
     * @param flockingSimulation Simulation to attach and run.
     */
    SimulationDialog(FlockingSimulation flockingSimulation) {
        this.simulation = flockingSimulation;
        stage = new Stage();
        stage.setTitle(TITLE);
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);

        Parent root;

        try {
            root = FXMLLoader.load(getClass().getResource("progress.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Button cancelButton = (Button) root.lookup("#cancelButton");
        cancelButton.setOnMouseClicked(event -> Platform.exit());

        ProgressBar progressBar = (ProgressBar) root.lookup("#progress");
        progressBar.progressProperty().bind(simulation.progressProperty);

        ImageView iconView = (ImageView) root.lookup("#icon");
        try {
            String path = getClass().getResource("clock.png").toURI().toString();
            iconView.setImage(new Image(path));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        stage.setScene(new Scene(root));
        stage.show();
        stage.requestFocus();

        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            simulation.run();
            long totalTime = System.currentTimeMillis() - startTime;
            System.out.println("Simulation time: " + totalTime / 1000 + " s");
            Platform.runLater(stage::close);
        }).start();
    }
}
