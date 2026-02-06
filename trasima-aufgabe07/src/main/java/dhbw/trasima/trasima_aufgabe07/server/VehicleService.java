package dhbw.trasima.trasima_aufgabe07.server;

import dhbw.trasima.trasima_aufgabe07.*;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Dieser Dienst verwaltet die Zustände der Fahrzeuge.
 * 
 * - Speichert Fahrzeugdaten in einer Liste (Map).
 * - publishPosition: Empfängt neue Positionsdaten und speichert sie.
 * - fetch: Liefert die Daten eines einzelnen Fahrzeugs zurück.
 * - fetchAll: Liefert die Daten aller bekannten Fahrzeuge zurück.
 */
public class VehicleService extends TrasimaServiceGrpc.TrasimaServiceImplBase {

    // Speicher für die Fahrzeugzustände (ID -> Zustand)
    private final Map<Integer, V2State> states = new ConcurrentHashMap<>();

    @Override
    public void publishPosition(V2State request, StreamObserver<Ack> responseObserver) {
        // Speichert den aktuellen Zustand des Fahrzeugs
        states.put(request.getId(), request);
        
        // Ausgabe auf der Konsole wie in trasima_bis_5 gefordert
        System.out.println("Fahrzeug " + request.getId() + " bei X=" + request.getX() + " Y=" + request.getY() + " Speed=" + request.getSpeed());
        
        // Bestätigung an den Client senden
        Ack ack = Ack.newBuilder().setMessage("Position für V2 " + request.getId() + " aktualisiert.").build();
        responseObserver.onNext(ack);
        responseObserver.onCompleted();
    }

    @Override
    public void fetch(V2Id request, StreamObserver<V2State> responseObserver) {
        // Sucht das Fahrzeug nach ID
        V2State state = states.get(request.getId());
        if (state != null) {
            responseObserver.onNext(state);
        } else {
            // Falls nicht gefunden, wird ein leeres Standard-Objekt gesendet
            responseObserver.onNext(V2State.getDefaultInstance());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void fetchAll(Empty request, StreamObserver<V2List> responseObserver) {
        // Erstellt eine Liste mit allen gespeicherten Fahrzeugen
        V2List list = V2List.newBuilder().addAllStates(states.values()).build();
        responseObserver.onNext(list);
        responseObserver.onCompleted();
    }
}
