package dhbw.trasima.trasima_aufgabe_10.client;

/**
 * Aufgabe 10 client-side API for sending full vehicle state to the REST server.
 *
 * <p>This is separate from the older {@code IPositionPublisher} interface (used by the simulation) because the
 * REST server stores additional fields like a direction/heading.</p>
 */
public interface IPublishPosition {

    /**
     * Creates/updates a vehicle state on the REST server.
     */
    void publishPosition(int id, double lat, double lon, double speed, double direction);

    /**
     * Deletes the vehicle resource on the REST server.
     */
    void deleteVehicle(int id);
}
