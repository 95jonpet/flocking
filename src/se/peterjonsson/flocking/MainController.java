package se.peterjonsson.flocking;

import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Stream;

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

        @SuppressWarnings("unchecked")
        ListView<Path> sidebar = (ListView<Path>) root.lookup("#sidebar");

        try (Stream<Path> stream = Files.list(FileSystems.getDefault().getPath("simulations"))) {
            Iterator<Path> iterator = stream.iterator();
            while (iterator.hasNext()) {
                sidebar.getItems().add(iterator.next());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        sidebar.getItems().sort(Comparator.naturalOrder());

        new SimulationDialog(simulation);
    }

}
