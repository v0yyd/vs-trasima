package dhbw.trasima.trasima_bis_5.tcp;

import dhbw.trasima.trasima_bis_5.IPositionPublisher;
import java.io.*;
import java.net.Socket;

public class TcpPositionPublisher implements IPositionPublisher {

    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;

    public TcpPositionPublisher(String host, int port) {
        try {
            this.socket = new Socket(host, port);
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException("TCP Verbindung fehlgeschlagen", e);
        }
    }

    @Override
    public synchronized void publishPosition(int id, double x, double y, double speed) {
        try {
            out.println("PUBLISH " + id + " " + x + " " + y + " " + speed);
            in.readLine(); // liest "OK"
        } catch (IOException e) {
            System.out.println("TCP Fehler: " + e.getMessage());
        }
    }
}
