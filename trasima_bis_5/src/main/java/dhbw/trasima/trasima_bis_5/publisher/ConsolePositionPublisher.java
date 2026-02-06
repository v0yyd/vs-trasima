package dhbw.trasima.trasima_bis_5.publisher;

import dhbw.trasima.trasima_bis_5.IPositionPublisher;

public class ConsolePositionPublisher implements IPositionPublisher {

    @Override
    public void publishPosition(int id, double x, double y, double speed) {
        System.out.printf(
                "V2-%d | Position: (%.2f, %.2f) | Geschwindigkeit: %.2f%n",
                id, x, y, speed
        );
    }
}
