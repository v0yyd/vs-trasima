package dhbw.trasima.trasima_aufgabe_10.server;

import dhbw.trasima.trasima_aufgabe_10.model.V2State;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Thread-safe in-memory store for {@link V2State} objects.
 *
 * <p>This is intentionally minimal: it backs the REST endpoints and supports concurrent updates from multiple
 * simulation threads.</p>
 */
final class InMemoryV2Store {

    // Concurrent map so multiple requests/threads can access/update safely.
    private final ConcurrentMap<Integer, V2State> store = new ConcurrentHashMap<>();

    List<V2State> list() {
        // Snapshot: callers get a copy, so they cannot modify the internal map via the returned list.
        return new ArrayList<>(store.values());
    }

    V2State get(int id) {
        return store.get(id);
    }

    boolean create(V2State state) {
        // Only create if absent (returns true if the id did not exist).
        return store.putIfAbsent(state.id, state) == null;
    }

    boolean update(V2State state) {
        // Only update if present (returns true if an entry existed).
        return store.replace(state.id, state) != null;
    }

    boolean delete(int id) {
        // Returns true if an entry existed and was removed.
        return store.remove(id) != null;
    }
}
