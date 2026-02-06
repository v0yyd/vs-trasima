package dhbw.trasima.trasima_aufgabe07.client;

import dhbw.trasima.trasima_aufgabe07.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * Ein einfacher gRPC-Client zum Abrufen von Fahrzeugdaten.
 * 
 * - Verbindet sich mit dem Server.
 * - Ruft alle Fahrzeugzustände ab und gibt sie aus.
 * - Ruft gezielt ein Fahrzeug (ID 1) ab und prüft, ob es existiert.
 */
public class DataClient {

    public static void main(String[] args) {
        // Kanal zum Server öffnen
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        // Stub für synchrone Aufrufe erstellen
        TrasimaServiceGrpc.TrasimaServiceBlockingStub stub = TrasimaServiceGrpc.newBlockingStub(channel);

        // 1. Alle Fahrzeuge abfragen
        System.out.println("--- Abfrage aller V2 Zustände ---");
        V2List allStates = stub.fetchAll(Empty.newBuilder().build());
        for (V2State state : allStates.getStatesList()) {
            System.out.println("V2 ID: " + state.getId() + ", X: " + state.getX() + ", Y: " + state.getY() + ", Speed: " + state.getSpeed());
        }

        // 2. Ein spezielles Fahrzeug abfragen
        System.out.println("\n--- Abfrage von Fahrzeug ID: 1 ---");
        V2State singleState = stub.fetch(V2Id.newBuilder().setId(1).build());
        if (singleState.getId() != 0) {
            System.out.println("V2 ID: " + singleState.getId() + ", X: " + singleState.getX() + ", Y: " + singleState.getY() + ", Speed: " + singleState.getSpeed());
        } else {
            System.out.println("Fahrzeug mit ID 1 nicht gefunden.");
        }

        // Verbindung schließen
        channel.shutdown();
    }
}
