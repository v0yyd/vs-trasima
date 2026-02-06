package dhbw.trasima.trasima_aufgabe06.server;

import dhbw.trasima.trasima_aufgabe06.TrasimaProto;
import dhbw.trasima.trasima_aufgabe06.TrasimaServiceGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TrasimaGrpcServer {

    private static final Map<Integer, TrasimaProto.V2State> store = new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder
                .forPort(50051)
                .addService(new Service())
                .build()
                .start();

        System.out.println("gRPC TRASIMA Server läuft auf Port 50051");
        server.awaitTermination();
    }

    static class Service extends TrasimaServiceGrpc.TrasimaServiceImplBase {

        @Override
        public void publish(TrasimaProto.V2State request,
                            StreamObserver<TrasimaProto.Ack> responseObserver) {

            store.put(request.getId(), request);

            responseObserver.onNext(
                    TrasimaProto.Ack.newBuilder()
                            .setMessage("OK")
                            .build()
            );
            responseObserver.onCompleted();
        }

        @Override
        public void fetch(TrasimaProto.V2Id request,
                          StreamObserver<TrasimaProto.V2State> responseObserver) {

            TrasimaProto.V2State state = store.get(request.getId());

            if (state != null) {
                responseObserver.onNext(state);
            }
            responseObserver.onCompleted();
        }
    }
}
