package dhbw.trasima.trasima_aufgabe07.client;

import dhbw.trasima.trasima_bis_5.IPositionPublisher;
import dhbw.trasima.trasima_aufgabe07.Ack;
import dhbw.trasima.trasima_aufgabe07.TrasimaServiceGrpc;
import dhbw.trasima.trasima_aufgabe07.V2State;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * Der gRPC-Client, der die Positionen an den Server sendet.
 * 
 * - Implementiert IPositionPublisher, um mit VirtualVehicle kompatibel zu sein.
 * - Öffnet eine Verbindung zum gRPC-Server.
 * - Wandelt die Daten in das gRPC-Format (V2State) um und sendet sie.
 */
public class PositionPublisher implements IPositionPublisher, AutoCloseable {

    private final ManagedChannel channel;
    private final TrasimaServiceGrpc.TrasimaServiceBlockingStub stub;

    public PositionPublisher(String host, int port) {
        // Verbindung zum Server aufbauen
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        // Client-Stub für den Aufruf der Methoden erstellen
        this.stub = TrasimaServiceGrpc.newBlockingStub(channel);
    }

    public void publishPosition(int id, double x, double y, double speed) {
        // gRPC-Nachricht bauen
        V2State state = V2State.newBuilder()
                .setId(id)
                .setX(x)
                .setY(y)
                .setSpeed(speed)
                .build();
        
        // Nachricht an den Server senden
        stub.publishPosition(state);
    }

    @Override
    public void close() {
        // Verbindung sauber schließen
        channel.shutdown();
    }
}
