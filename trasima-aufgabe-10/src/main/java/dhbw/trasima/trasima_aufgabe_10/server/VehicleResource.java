package dhbw.trasima.trasima_aufgabe_10.server;

import dhbw.trasima.trasima_aufgabe_10.model.V2State;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * JAX-RS resource that exposes CRUD operations for vehicle states via JSON.
 *
 * <p>Base path: {@code /api/trasima/vehicles}</p>
 *
 * <p>Notes:</p>
 * <ul>
 *   <li>This is a simple in-memory demo; data is lost on server restart.</li>
 *   <li>The vehicle id is part of the URL and always overrides any id in the JSON body.</li>
 * </ul>
 */
@Path("/trasima/vehicles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VehicleResource {

    // One shared store for the whole JVM (sufficient for this exercise).
    private static final InMemoryV2Store STORE = new InMemoryV2Store();

    /**
     * Lists all currently known vehicles.
     */
    @GET
    public List<V2State> list() {
        return STORE.list();
    }

    /**
     * Fetches a single vehicle by id.
     *
     * @return 200 with JSON body, or 404 if the vehicle does not exist
     */
    @GET
    @Path("{id}")
    public Response get(@PathParam("id") int id) {
        V2State state = STORE.get(id);
        if (state == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(state).build();
    }

    /**
     * Creates a vehicle state (id comes from the URL).
     *
     * @return 201 if created, 409 if the id already exists, 400 if body is missing
     */
    @POST
    @Path("{id}")
    public Response create(@PathParam("id") int id, V2State state) {
        if (state == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing JSON body").build();
        }
        // Ensure the canonical id is the one from the URL.
        state.id = id;

        boolean created = STORE.create(state);
        if (!created) {
            return Response.status(Response.Status.CONFLICT).entity("Vehicle ID already exists").build();
        }
        return Response.status(Response.Status.CREATED).entity(state).build();
    }

    /**
     * Updates a vehicle state (id comes from the URL).
     *
     * @return 200 if updated, 404 if the vehicle does not exist, 400 if body is missing
     */
    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") int id, V2State state) {
        if (state == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing JSON body").build();
        }
        // Ensure the canonical id is the one from the URL.
        state.id = id;

        boolean updated = STORE.update(state);
        if (!updated) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(state).build();
    }

    /**
     * Deletes a vehicle by id.
     *
     * @return 204 if deleted, 404 if the vehicle does not exist
     */
    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") int id) {
        boolean deleted = STORE.delete(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
