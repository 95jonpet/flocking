package se.peterjonsson.flocking;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Custom image writer for concurrent writing.
 * Adapted from https://blog.idrsolutions.com/2014/10/imageio-write-executorservice-io-bound-applications-java/
 *
 * @author Leon Atherton
 * @author Peter Jonsson <95jonpet@gmail.com>
 */
class CustomImageWriter {

    /**
     * Runnable executor.
     */
    private ExecutorService executor;

    /**
     * Creates a new image writer.
     */
    CustomImageWriter() {
        this.executor = Executors.newFixedThreadPool(2);
    }

    /**
     * Writes an image concurrently.
     * @param image The image to write.
     * @param path The file path to write to.
     * @throws IOException File write fail.
     */
    void writeImage(final BufferedImage image, final Path path) throws IOException {
        executor.submit(() -> {
            try (BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(path.toFile()))) {
                ImageIO.write(image, "JPG", output);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Waits for all images to be written and kills the {@link #executor}.
     * This process is allowed a maximum execution time of 1 minute.
     */
    void waitForImages() {
        executor.shutdown();

        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}