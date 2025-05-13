package com.example.storeservice.service;

import com.example.storeservice.dto.StoreResponseDTO;
import com.example.storeservice.event.StoreCreatedMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class StoreSqsMessagingService {

    private final SqsClient sqs;
    private final ObjectMapper mapper;

    // SQS 콘솔에서 미리 생성한 큐 URL
    @Value("${aws.sqs.url.store-add}")
    private String storeAddQueueUrl;
    @Value("${aws.sqs.url.store-update}")
    private String storeUpdateQueueUrl;
    @Value("${aws.sqs.url.store-delete}")
    private String storeDeleteQueueUrl;

    public void sendAddEvent(StoreCreatedMessage dto) throws JsonProcessingException {
        sendEvent(storeAddQueueUrl, "store-add", dto);
    }

    public void sendUpdateEvent(StoreCreatedMessage dto) throws JsonProcessingException {
        sendEvent(storeUpdateQueueUrl, "store-update", dto);
    }

    public void sendDeleteEvent(StoreCreatedMessage dto) throws JsonProcessingException {
        sendEvent(storeDeleteQueueUrl, "store-delete", dto);
    }

    private void sendEvent(String queueUrl, String eventType, StoreCreatedMessage dto)
            throws JsonProcessingException {
        String body = mapper.writeValueAsString(dto);
        SendMessageRequest req = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(body)
                .messageAttributes(Map.of(
                        "eventType", MessageAttributeValue.builder()
                                .dataType("String")
                                .stringValue(eventType)
                                .build()
                ))
                .build();
        sqs.sendMessage(req);
    }
}
