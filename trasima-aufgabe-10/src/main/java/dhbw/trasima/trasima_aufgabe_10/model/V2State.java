package dhbw.trasima.trasima_aufgabe_10.model;

/**
 * Simple JSON DTO representing the state of one simulated vehicle.
 *
 * <p>Fields are public so Jackson (Jersey's JSON binding) can serialize/deserialize without extra boilerplate.</p>
 */
public class V2State {

    /** Vehicle id (comes from the REST path {@code /vehicles/{id}}). */
    public int id;
    /** Latitude / X coordinate (depending on your simulation interpretation). */
    public double lat;
    /** Longitude / Y coordinate (depending on your simulation interpretation). */
    public double lon;
    /** Speed in arbitrary units (as provided by the simulation). */
    public double speed;
    /** Movement direction in degrees (0..360), where 0 points to +X (east) for the simple atan2() computation. */
    public double direction;

    // Default constructor required by Jackson for JSON deserialization.
    public V2State() {
    }

    public V2State(int id, double lat, double lon, double speed, double direction) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.speed = speed;
        this.direction = direction;
    }
}
