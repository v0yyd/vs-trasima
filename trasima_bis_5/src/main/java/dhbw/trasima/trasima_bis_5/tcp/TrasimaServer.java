package dhbw.trasima.trasima_bis_5.tcp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TrasimaServer {

    public static void main(String[] args) throws Exception {

        IV2Store store = new InMemoryV2Store();
        ServerSocket serverSocket = new ServerSocket(5555);
        ExecutorService pool = Executors.newCachedThreadPool();

        System.out.println("TRASIMA Server läuft auf Port 5555");

        while (true) {
            Socket client = serverSocket.accept();   // blockierend
            pool.submit(() -> {
                try {
                    handleClient(client, store);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private static void handleClient(Socket socket, IV2Store store) throws Exception {

        try (
                Socket s = socket;
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                PrintWriter out = new PrintWriter(s.getOutputStream(), true)
        ) {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                System.out.println("Request: " + line);

                String[] parts = line.split(" ");

                if (parts[0].equals("PUBLISH") && parts.length >= 5) {
                    int id = Integer.parseInt(parts[1]);
                    double x = Double.parseDouble(parts[2]);
                    double y = Double.parseDouble(parts[3]);
                    double speed = Double.parseDouble(parts[4]);

                    store.save(new V2State(id, x, y, speed));
                    out.println("OK");
                    continue;
                }

                if (parts[0].equals("FETCH") && parts.length >= 2) {
                    int id = Integer.parseInt(parts[1]);
                    V2State st = store.get(id);

                    if (st == null) {
                        out.println("NOT_FOUND");
                    } else {
                        out.println(st.id + " " + st.x + " " + st.y + " " + st.speed);
                    }
                    continue;
                }

                out.println("ERROR");
            }
        }
    }
}
