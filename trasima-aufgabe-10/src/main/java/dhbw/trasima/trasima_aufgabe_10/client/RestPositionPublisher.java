package dhbw.trasima.trasima_aufgabe_10.client;

import dhbw.trasima.trasima_bis_5.IPositionPublisher;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Adapter that takes position updates from the simulation ({@link IPositionPublisher}) and forwards them to the
 * Aufgabe 10 REST server.
 *
 * <p>Publishing strategy:</p>
 * <ul>
 *   <li>First update for a vehicle id: try {@code POST /vehicles/{id}} (create).</li>
 *   <li>Later updates: use {@code PUT /vehicles/{id}} (update).</li>
 *   <li>If the server replies with 409 on POST, we fall back to PUT (vehicle already exists).</li>
 * </ul>
 *
 * <p>This class is thread-safe because VV instances publish concurrently.</p>
 */
public final class RestPositionPublisher implements IPositionPublisher, IPublishPosition {

    // Reused HTTP client instance (keeps connections open and avoids per-request setup cost).
    private final HttpClient httpClient;
    private final String baseUrl;

    // Tracks which ids we have already created successfully (to decide between POST and PUT).
    private final Set<Integer> createdIds = ConcurrentHashMap.newKeySet();

    // Remember the last position per vehicle to compute a direction from movement (dx/dy).
    private final Map<Integer, LastPos> lastPositions = new ConcurrentHashMap<>();

    public RestPositionPublisher(String baseUrl) {
        // Normalize baseUrl so we don't end up with double slashes when building URIs.
        this.baseUrl = trimTrailingSlash(baseUrl);
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
    }

    @Override
    public void publishPosition(int id, double x, double y, double speed) {
        // VirtualVehicle publishes only x/y/speed; we derive a heading direction from the last movement vector.
        double direction = computeDirectionDegrees(id, x, y);
        publishPosition(id, x, y, speed, direction);
    }

    @Override
    public void publishPosition(int id, double lat, double lon, double speed, double direction) {
        String json = toJson(id, lat, lon, speed, direction);

        // Optimistic "create once": the first publish tries POST.
        if (createdIds.add(id)) {
            int status = post(id, json);
            if (status == 201) {
                return;
            }
            // 409 means the vehicle already exists (e.g., previous run); switch to PUT for updates.
            if (status != 409) {
                // Any other error: do not mark as created, so we can retry POST later.
                createdIds.remove(id);
                return;
            }
        }

        // Normal updates go through PUT; 404 indicates the resource is gone and must be re-created.
        int status = put(id, json);
        if (status == 404) {
            createdIds.remove(id);
        }
    }

    @Override
    public void deleteVehicle(int id) {
        // Best-effort delete; ignore status code because this is cleanup.
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/trasima/vehicles/" + id))
                .timeout(Duration.ofSeconds(2))
                .DELETE()
                .build();
        send(request);
        createdIds.remove(id);
        lastPositions.remove(id);
    }

    private int post(int id, String json) {
        // POST = create resource for id.
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/trasima/vehicles/" + id))
                .timeout(Duration.ofSeconds(2))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        return send(request);
    }

    private int put(int id, String json) {
        // PUT = replace full state for id (idempotent update).
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/trasima/vehicles/" + id))
                .timeout(Duration.ofSeconds(2))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();
        return send(request);
    }

    private int send(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode();
        } catch (Exception e) {
            // Keep the simulation running even if the REST server is temporarily unavailable.
            System.out.println("REST Fehler: " + e.getMessage());
            return -1;
        }
    }

    private double computeDirectionDegrees(int id, double x, double y) {
        // Store the new position and get the previous one (atomic via ConcurrentHashMap#put).
        LastPos previous = lastPositions.put(id, new LastPos(x, y));
        if (previous == null) {
            return 0.0;
        }
        double dx = x - previous.x;
        double dy = y - previous.y;
        if (dx == 0.0 && dy == 0.0) {
            return 0.0;
        }
        // atan2 returns the angle of the vector (dx,dy) in radians; convert to degrees and normalize to [0,360).
        double radians = Math.atan2(dy, dx);
        double degrees = Math.toDegrees(radians);
        return degrees < 0.0 ? degrees + 360.0 : degrees;
    }

    private static String toJson(int id, double lat, double lon, double speed, double direction) {
        // Minimal JSON builder (no Jackson on client side).
        return "{"
                + "\"id\":" + id + ","
                + "\"lat\":" + lat + ","
                + "\"lon\":" + lon + ","
                + "\"speed\":" + speed + ","
                + "\"direction\":" + direction
                + "}";
    }

    private static String trimTrailingSlash(String s) {
        if (s == null || s.isBlank()) {
            return "http://localhost:8080";
        }
        return s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
    }

    /**
     * Small immutable value object to keep the last (x,y) position per vehicle.
     */
    private record LastPos(double x, double y) {
    }
}
