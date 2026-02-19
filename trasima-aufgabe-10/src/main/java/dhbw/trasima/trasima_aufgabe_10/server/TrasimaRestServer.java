package dhbw.trasima.trasima_aufgabe_10.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * Boots a small embedded HTTP server (Jetty) and mounts a Jersey (JAX-RS) REST API under {@code /api/*}.
 *
 * <p>The actual REST endpoints are implemented in {@link VehicleResource}.</p>
 *
 * <p>CLI args:</p>
 * <ul>
 *   <li>{@code --httpPort <port>} (default: {@code 8080})</li>
 * </ul>
 */
public final class TrasimaRestServer {

    public static void main(String[] args) throws Exception {
        // Parse CLI args (simple key/value parsing; no external library).
        int httpPort = intArg(args, "--httpPort", 8080);

        // Jersey resource configuration: register our resource class and JSON (Jackson) support.
        ResourceConfig config = new ResourceConfig();
        config.register(VehicleResource.class);
        config.register(JacksonFeature.class);

        // Jetty server bound to the chosen port.
        Server server = new Server(httpPort);

        // Minimal servlet context; we do not use sessions for this exercise.
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");

        // Bridge Jetty <-> Jersey: the ServletContainer dispatches HTTP requests to JAX-RS resources.
        ServletHolder servlet = new ServletHolder(new ServletContainer(config));
        servlet.setInitOrder(0);
        context.addServlet(servlet, "/api/*");

        // Start server and block the main thread.
        server.setHandler(context);
        server.start();
        System.out.println("REST Server: http://localhost:" + httpPort + "/api/trasima/vehicles");
        server.join();
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
