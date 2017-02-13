package sample;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Controller {
    private FlockingSimulation simulation;
    private ImageView imageView;

    public Controller(Parent root) {
        imageView = (ImageView) root.lookup("#imageview");
        //imageView.prefWidth(FlockingSimulation.SIZE);
        //imageView.prefHeight(FlockingSimulation.SIZE);

        simulation = new FlockingSimulation(42);

        Platform.runLater(() -> {
            simulation.run();

            Image img = simulation.getFrame(FlockingSimulation.STEPS - 1).getImage();
            imageView.setImage(img);
        });

        Slider slider = (Slider) root.lookup("#slider");
        slider.setMin(0);
        slider.setMax(FlockingSimulation.STEPS - 1);
        slider.setValue(0);
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            Image img = simulation.getFrame((int) Math.floor((double) newValue)).getImage();
            imageView.setImage(img);
        });
    }

}
