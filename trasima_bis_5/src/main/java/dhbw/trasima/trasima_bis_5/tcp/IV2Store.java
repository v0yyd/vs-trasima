package dhbw.trasima.trasima_bis_5.tcp;

public interface IV2Store {

    void save(V2State state);

    V2State get(int id);
}
