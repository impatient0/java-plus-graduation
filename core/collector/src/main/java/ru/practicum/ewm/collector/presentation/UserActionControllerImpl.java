package ru.practicum.ewm.collector.presentation;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import ru.practicum.ewm.collector.infrastructure.mapper.UserActionMapper;
import ru.practicum.ewm.stats.grpc.UserActionControllerGrpc;
import ru.practicum.ewm.stats.grpc.UserActionProto;
import ru.practicum.ewm.stats.kafka.UserActionAvro;

@GrpcService
@Slf4j
public class UserActionControllerImpl extends UserActionControllerGrpc.UserActionControllerImplBase {

    @Value("${kafka.topic.user-actions}")
    private final String userActionsTopic;

    private final KafkaTemplate<String, UserActionAvro> kafkaTemplate;
    private final UserActionMapper mapper;

    public UserActionControllerImpl(
        @Value("${kafka.topic.user-actions}") String userActionsTopic,
        KafkaTemplate<String, UserActionAvro> kafkaTemplate,
        UserActionMapper mapper) {
        this.userActionsTopic = userActionsTopic;
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
    }

    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver) {
        log.info("Received user action via gRPC: {}", request);

        UserActionAvro avroMessage = mapper.toAvro(request);

        kafkaTemplate.send(userActionsTopic, avroMessage);
        log.info("Sent user action to Kafka topic '{}'", userActionsTopic);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}