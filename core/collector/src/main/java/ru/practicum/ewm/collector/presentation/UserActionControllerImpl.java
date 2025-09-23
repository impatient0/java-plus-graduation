package ru.practicum.ewm.collector.presentation;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.ewm.collector.infrastructure.kafka.UserActionProducer;
import ru.practicum.ewm.collector.infrastructure.mapper.UserActionMapper;
import ru.practicum.ewm.stats.grpc.UserActionControllerGrpc;
import ru.practicum.ewm.stats.grpc.UserActionProto;
import ru.practicum.ewm.stats.kafka.UserActionAvro;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class UserActionControllerImpl extends UserActionControllerGrpc.UserActionControllerImplBase {

    private final UserActionProducer producer;
    private final UserActionMapper mapper;

    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver) {
        log.info("Received user action via gRPC: {}", request);

        UserActionAvro avroMessage = mapper.toAvro(request);

        producer.sendUserAction(avroMessage);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}