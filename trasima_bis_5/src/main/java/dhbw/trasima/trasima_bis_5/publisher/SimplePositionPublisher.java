package dhbw.trasima.trasima_bis_5.publisher;

import dhbw.trasima.trasima_bis_5.IPositionPublisher;

public class SimplePositionPublisher implements IPositionPublisher {

    @Override
    public void publishPosition(int id, double x, double y, double speed) {
        System.out.println(
                "Fahrzeug " + id + " bei X=" + x + " Y=" + y
        );
    }
}
