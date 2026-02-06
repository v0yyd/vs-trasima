package dhbw.trasima.trasima_bis_5;

import dhbw.trasima.trasima_bis_5.tcp.TcpPositionPublisher;

public class Main {

    public static void main(String[] args) {
        int numberOfVehicles = Integer.parseInt(args[0]);

        System.out.println("Starte Simulation mit " + numberOfVehicles + " Virtual Vehicles.");

        // IPositionPublisher publisher = new ConsolePositionPublisher();
        //IPositionPublisher publisher = new SimplePositionPublisher();
        //IPositionPublisher publisher = new TcpPositionPublisher();

        Thread[] threads = new Thread[numberOfVehicles];

        for (int i = 0; i < numberOfVehicles; i++) {

            IPositionPublisher publisher =
                    new TcpPositionPublisher("localhost", 5555);

            VirtualVehicle v2 = new VirtualVehicle(
                    i + 1,
                    Math.random() * 10,
                    Math.random() * 10,
                    0.5 + Math.random(),
                    publisher
            );

            threads[i] = new Thread(v2);
            threads[i].start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Simulation abgeschlossen.");
    }
}
