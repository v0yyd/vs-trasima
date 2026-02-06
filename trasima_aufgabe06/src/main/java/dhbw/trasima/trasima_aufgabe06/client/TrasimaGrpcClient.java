package dhbw.trasima.trasima_aufgabe06.client;

import dhbw.trasima.trasima_aufgabe06.TrasimaProto;
import dhbw.trasima.trasima_aufgabe06.TrasimaServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class TrasimaGrpcClient {

    public static void main(String[] args) {

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        TrasimaServiceGrpc.TrasimaServiceBlockingStub stub =
                TrasimaServiceGrpc.newBlockingStub(channel);

        stub.publish(
                TrasimaProto.V2State.newBuilder()
                        .setId(1)
                        .setX(10)
                        .setY(20)
                        .setSpeed(1.5)
                        .build()
        );

        TrasimaProto.V2State state =
                stub.fetch(TrasimaProto.V2Id.newBuilder().setId(1).build());

        System.out.println("Antwort: " + state);

        channel.shutdown();
    }
}
