package se.peterjonsson.flocking;

import javafx.scene.Parent;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controller for the main view.
 */
class MainController {
    /**
     * Simulation to run.
     */
    private final FlockingSimulation simulation;

    /**
     * Image view displaying a rendered simulation.
     */
    private final ImageView imageView;

    /**
     * Creates a new controller for the main view.
     * @param root Root element of the view.
     */
    MainController(Parent root) {
        imageView = (ImageView) root.lookup("#imageView");

        simulation = new FlockingSimulation(42);

        Slider slider = (Slider) root.lookup("#slider");
        slider.setMin(0);
        slider.setMax(FlockingSimulation.STEPS - 1);
        slider.setValue(0);
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            Image img = simulation.getFrame((int) Math.floor((double) newValue)).getImage();
            imageView.setImage(img);
        });

        new SimulationDialog(simulation);
    }

}
