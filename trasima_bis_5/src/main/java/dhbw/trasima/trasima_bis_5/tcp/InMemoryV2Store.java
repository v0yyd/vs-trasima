package dhbw.trasima.trasima_bis_5.tcp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryV2Store implements IV2Store {

    private final Map<Integer, V2State> map = new ConcurrentHashMap<>();

    @Override
    public void save(V2State state) {
        map.put(state.id, state);
    }

    @Override
    public V2State get(int id) {
        return map.get(id);
    }
}
