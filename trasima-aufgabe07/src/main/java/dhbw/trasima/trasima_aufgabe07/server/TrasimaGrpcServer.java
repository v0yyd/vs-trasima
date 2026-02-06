package dhbw.trasima.trasima_aufgabe07.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;

/**
 * Der gRPC Server für die Trasima-Anwendung.
 * 
 * - Startet den Server auf einem bestimmten Port (50051).
 * - Registriert den VehicleService, um Anfragen zu bearbeiten.
 * - Hält den Server am Laufen, bis er manuell gestoppt wird.
 */
public class TrasimaGrpcServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 50051;
        // Erstellt den Server und fügt den Dienst hinzu
        Server server = ServerBuilder.forPort(port)
                .addService(new VehicleService())
                .build();

        System.out.println("Starte gRPC Server auf Port " + port);
        server.start();
        System.out.println("Server gestartet.");
        // Wartet, bis der Server beendet wird
        server.awaitTermination();
    }
}
