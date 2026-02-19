package dhbw.trasima.trasima_aufgabe_10.client;

import dhbw.trasima.trasima_bis_5.VirtualVehicle;

/**
 * Starts a local simulation with multiple {@link VirtualVehicle} instances and publishes their positions via REST.
 *
 * <p>Each vehicle runs in its own thread and calls the provided publisher on every simulation tick.</p>
 *
 * <p>CLI args:</p>
 * <ul>
 *   <li>{@code --vehicles <n>} (default: {@code 10})</li>
 *   <li>{@code --baseUrl <url>} (default: {@code http://localhost:8080})</li>
 * </ul>
 */
public final class RestSimulationClientMain {

    public static void main(String[] args) {
        // Parse CLI args (simple key/value parsing; no external library).
        int numberOfVehicles = intArg(args, "--vehicles", 10);
        String baseUrl = stringArg(args, "--baseUrl", "http://localhost:8080");

        // Publisher that translates "position updates" into HTTP POST/PUT requests.
        RestPositionPublisher publisher = new RestPositionPublisher(baseUrl);
        Thread[] threads = new Thread[numberOfVehicles];

        System.out.println("Starte REST Simulation mit " + numberOfVehicles + " Virtual Vehicles.");
        System.out.println("REST Target: " + baseUrl + "/api/trasima/vehicles/{id}");

        for (int i = 0; i < numberOfVehicles; i++) {
            int id = i + 1;
            // Create a vehicle with random start position and speed.
            VirtualVehicle v2 = new VirtualVehicle(
                    id,
                    Math.random() * 10,
                    Math.random() * 10,
                    0.5 + Math.random(),
                    publisher
            );
            // Run each vehicle concurrently.
            threads[i] = new Thread(v2);
            threads[i].start();
        }

        // Wait until all vehicle threads stop (VirtualVehicle decides when it finishes).
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Cleanup: delete all created vehicle resources on the REST server.
        for (int i = 0; i < numberOfVehicles; i++) {
            publisher.deleteVehicle(i + 1);
        }

        System.out.println("Simulation abgeschlossen (Fahrzeuge gelöscht).");
    }

    private static int intArg(String[] args, String key, int defaultValue) {
        String value = stringArg(args, key, null);
        if (value == null) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }

    private static String stringArg(String[] args, String key, String defaultValue) {
        // Looks for "<key> <value>" pairs in the raw args array.
        for (int i = 0; i < args.length - 1; i++) {
            if (key.equals(args[i])) {
                return args[i + 1];
            }
        }
        return defaultValue;
    }
}
