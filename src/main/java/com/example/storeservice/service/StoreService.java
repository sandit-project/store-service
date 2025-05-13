package com.example.storeservice.service;

import com.example.storeservice.domain.Store;
import com.example.storeservice.dto.*;
import com.example.storeservice.event.OrderCreatedMessage;
import com.example.storeservice.event.StoreCreatedMessage;
import com.example.storeservice.exception.StoreAlreadyExistsException;
import com.example.storeservice.exception.StoreNotFoundException;
import com.example.storeservice.repository.StoreRepository;
import com.example.storeservice.type.OrderStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final RabbitTemplate rabbitTemplate; // RabbitMQ 직접 접근용
    private final ObjectMapper objectMapper;
    private final StoreSqsMessagingService storeSqsMessagingService;

    //지점 목록 조회(커서방식)
    @Transactional
    public StoreListResponseDTO getStoresByCursor(int limit, Long lastUid) {
        List<Store> stores;

        //처음 요청일 때(lastUid가 null인 경우
        if (lastUid == null) {
            stores = storeRepository.findFirstByOrderByUidAsc(limit);

        } else {
            //lastUid 이후로 데이터 가져오기
            stores = storeRepository.findByUidGreaterThanOrderByUidAsc(lastUid, limit);
        }

        //StoreResponseDTO로 변환
        List<StoreResponseDTO> storeResponseDTOS = stores.stream()
                .map(store -> store.toStoreResponseDTO())
                .collect(Collectors.toList());

        //마지막 페이지 여부 계산
        boolean lastPage = stores.size() < limit;

        //nextCursor 계산:마지막 요소의 uid를 nextCursor로 설정
        Long nextCursor = stores.isEmpty() ? null : stores.get(stores.size() - 1).getStoreUid();


        return StoreListResponseDTO.builder()
                .storeList(storeResponseDTOS)
                .lastPage(lastPage)
                .nextCursor(nextCursor)
                .build();


    }

    // 지점 이름으로 지점 조회
    public StoreResponseDTO viewStore(Long storeUid) throws StoreNotFoundException {
        Store store = storeRepository.findByStoreUid(storeUid)
                .orElseThrow(() -> new StoreNotFoundException(storeUid));
        return store.toStoreResponseDTO();
    }

    // 매니저 UID로 지점 UID조회
    public Long getStoreUidByManagerUid(Long managerUid) {
        return storeRepository
                .findByManagerUid(managerUid)
                .orElseThrow(()->new NoSuchElementException("매니저 UID에 해당하는 UID를 찾을 수가 없습니다. "))
                .getStoreUid();
    }

    //지점 추가
    public StoreResponseDTO addStore(@Valid StoreCreatedMessage storeRequestDTO) throws StoreAlreadyExistsException, IOException {
        if (storeRepository.existsByStoreName(storeRequestDTO.getStoreName())) {
            throw new StoreAlreadyExistsException(storeRequestDTO.getStoreName());
        }
       /** 직접 DB에 저장
        Store store = Store.builder()
                .storeName(storeRequestDTO.getStoreName())
                .managerUid(storeRequestDTO.getManagerUid())
                .storeAddress(storeRequestDTO.getStoreAddress())
                .storePostcode(storeRequestDTO.getStorePostcode())
                .storeLatitude(storeRequestDTO.getStoreLatitude())
                .storeLongitude(storeRequestDTO.getStoreLongitude())
                .storeStatus(storeRequestDTO.getStoreStatus())
                .build();

        Store saveStore = storeRepository.save(store);//DB에 저장해 줌.
        */
       // 1) 메세지용 DTO로 변환
        StoreCreatedMessage msg = StoreCreatedMessage.builder()
                .storeName(storeRequestDTO.getStoreName())
                .managerUid(storeRequestDTO.getManagerUid())
                .storeAddress(storeRequestDTO.getStoreAddress())
                .storePostcode(storeRequestDTO.getStorePostcode())
                .storeLatitude(storeRequestDTO.getStoreLatitude())
                .storeLongitude(storeRequestDTO.getStoreLongitude())
                .build();

        // 2) SQS로 발행(로컬테스트용)
        storeSqsMessagingService.sendAddEvent(msg);


        // 3) 즉시 응답 : 임시로 DTO에 입력값만 그대로 리턴
        return StoreResponseDTO.builder()
                .storeName(msg.getStoreName())
                .managerUid(msg.getManagerUid())
                .storeAddress(msg.getStoreAddress())
                .storePostcode(msg.getStorePostcode())
                .storeLatitude(msg.getStoreLatitude())
                .storeLongitude(msg.getStoreLongitude())
                .build();
    }


    //지점 수정
    @Transactional
    public StoreResponseDTO updateStore(Long storeUid, StoreCreatedMessage storeCreatedMessage) throws StoreNotFoundException, JsonProcessingException {
        Store existingStore = storeRepository.findByStoreUid(storeUid)
                .orElseThrow(() -> new StoreNotFoundException(storeUid));

        Store updateStore = Store.builder()
                .storeUid(existingStore.getStoreUid())
                .storeName(storeCreatedMessage.getStoreName())
                .managerUid(storeCreatedMessage.getManagerUid())
                .storeAddress(storeCreatedMessage.getStoreAddress())
                .storePostcode(storeCreatedMessage.getStorePostcode())
                .storeLatitude(storeCreatedMessage.getStoreLatitude())
                .storeLongitude(storeCreatedMessage.getStoreLongitude())
                .storeStatus(storeCreatedMessage.getStoreStatus())
                .storeCreatedDate(existingStore.getStoreCreatedDate())
                .version(existingStore.getVersion())//버전 증가
                .build();
        // Store saveStore = storeRepository.save(updateStore);

        // 1) 메세지용 DTO로 변환
        StoreCreatedMessage msg = StoreCreatedMessage.builder()
                .storeName(storeCreatedMessage.getStoreName())
                .managerUid(storeCreatedMessage.getManagerUid())
                .storeAddress(storeCreatedMessage.getStoreAddress())
                .storePostcode(storeCreatedMessage.getStorePostcode())
                .storeLatitude(storeCreatedMessage.getStoreLatitude())
                .storeLongitude(storeCreatedMessage.getStoreLongitude())
                .build();
        storeSqsMessagingService.sendUpdateEvent(msg);

        //return saveStore.toStoreResponseDTO();
        return updateStore.toStoreResponseDTO();

    }


    //지점 상태 변경
    public void updateStatusStore (Long storeUid, String status) throws StoreNotFoundException {
        if (!storeRepository.existsById(storeUid)) {
            throw new StoreNotFoundException(storeUid);
        }
        storeRepository.updateStatusByUid(storeUid, status);
        rabbitTemplate.convertAndSend("store-update.store-service",status);
    }

    //지점 삭제
    public void deleteStore (Long storeUid){
        if (!storeRepository.existsById(storeUid)) {
            throw new StoreNotFoundException(storeUid);
        }
        storeRepository.deleteByUid(storeUid);
        rabbitTemplate.convertAndSend("store-delete.store-service", storeUid);
    }


    // 주문 조작 함수
    public RabbitResponseDTO remoteOrderInQueue(String action, OrderCreatedMessage message){
        OrderCreatedMessage received = null;

        switch (action){
            case "confirm":
                received = confirmOrder(message);
                break;
            case "cooking":
                received = prepareOrder(message);
                break;
            case "cancel":
                received = cancelOrder(message);
                break;
            default:
                break;
        }

        if (received != null) {
            log.info("큐로 보낸 메시지: {}", received);
            return RabbitResponseDTO.builder()
                    .isSuccess(true)
                    .message("성공했습니다.")
                    .build();
        } else {
            log.warn("큐로 보내기 실패한 메시지: {}", received);
            return RabbitResponseDTO.builder()
                    .isSuccess(false)
                    .message("실패했습니다!!")
                    .build();
        }
    }

    // 주문 수락
    private OrderCreatedMessage confirmOrder(OrderCreatedMessage message) {
        try {
            // 1. 큐에서 메시지 수동 소비 (ex: order-preparing)
            // Object message = rabbitTemplate.receiveAndConvert("order-created.order-service");
            // OrderCreatedMessage received = objectMapper.convertValue(message, OrderCreatedMessage.class);

            message.setStatus(OrderStatus.ORDER_CONFIRMED);

            // 메시지 전송
            rabbitTemplate.convertAndSend("status-change.order-service", message);
            return message;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 주문 준비
    private OrderCreatedMessage prepareOrder(OrderCreatedMessage message) {
        try {
            // 1. 큐에서 메시지 수동 소비 (ex: order-preparing)
            // Object message = rabbitTemplate.receiveAndConvert("order-accepted.order-service");
            // OrderCreatedMessage received = objectMapper.convertValue(message, OrderCreatedMessage.class);

            message.setStatus(OrderStatus.ORDER_COOKING);

            // 메시지 전송
            rabbitTemplate.convertAndSend("status-change.order-service", message);
            return message;
        } catch (Exception e) {
            return null;
        }
    }

    // 주문 취소
    private OrderCreatedMessage cancelOrder(OrderCreatedMessage message) {
        // 주문 수락이나 주문 생성중 어디서 취소 됬는지 확인 필요함

        try {
            // 1. 큐에서 메시지 수동 소비 (ex: order-preparing)
            // Object message = rabbitTemplate.receiveAndConvert("order-created.order-service");
            // OrderCreatedMessage received = objectMapper.convertValue(message, OrderCreatedMessage.class);

            message.setStatus(OrderStatus.ORDER_CANCELLED);

            // 메시지 전송
            rabbitTemplate.convertAndSend("status-change.order-service", message);
            return message;
        } catch (Exception e) {
            return null;
        }
    }
}


