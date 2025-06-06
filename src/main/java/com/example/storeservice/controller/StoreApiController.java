package com.example.storeservice.controller;

import com.example.storeservice.dto.*;
import com.example.storeservice.event.OrderCreatedMessage;
import com.example.storeservice.exception.StoreAlreadyExistsException;
import com.example.storeservice.service.StoreService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stores")
@Slf4j
public class StoreApiController {

    private final StoreService storeService;

    @GetMapping("/check-manager")
    public ResponseEntity<HashMap<Object, Object>> checkManagerAssigned(@RequestParam Long userUid) {
        boolean isAssigned = storeService.isManagerAssigned(userUid);
        HashMap<Object, Object> response = new HashMap<>();
        response.put("assigned", isAssigned);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/manager-mapping")
    public List<ManagerMappingDTO> getManagerMapping() {
        return storeService.getManagerMapping();
    }


    @GetMapping
    public List<CustomerStoreListResponseDTO> getStores() {
        return storeService.getStores();
    }

    //지점 목록 조회
    @GetMapping("/list")
    public StoreListResponseDTO getAllStores(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Long lastUid
             ){
        // 지점 목록 가져오기
        System.out.println("lastUid: " + lastUid);
        System.out.println("limit: " + limit);
        StoreListResponseDTO test = storeService.getStoresByCursor(limit, lastUid);
        log.info("지점 리스트 : {}",test.getStoreList().get(0).getStoreName());
        return  test;
    }

    //지점 uid로 지점 조회
    @GetMapping("/{storeUid}")
    public StoreResponseDTO getStore(@PathVariable(name="storeUid") Long storeUid) {
        return storeService.viewStore(storeUid);
    }

    /**
     * 매니저 UID 로 해당 지점 UID,storeName 반환
     * GET /stores/storeUid?userUid=123
     */
    @GetMapping("/storeUid")
    public StoreUidResponseDTO getStoreUidByManagerUid(@RequestParam(name = "userUid") Long userUid) {
        Long storeUid = storeService.getStoreUidByManagerUid(userUid);
        String storeName = storeService.viewStore(storeUid).getStoreName();
        return StoreUidResponseDTO.builder()
                .storeUid(storeUid)
                .storeName(storeName)
                .build();
    }

    //지점 추가
    @PostMapping
    public ResponseEntity<StoreResponseDTO> addStore(@Valid @RequestBody StoreRequestDTO storeRequestDTO) throws StoreAlreadyExistsException, IOException {
        StoreResponseDTO response = storeService.addStore(storeRequestDTO);
        log.info("지점 등록 요청 정보 : {}",storeRequestDTO.getStoreName());
        return ResponseEntity.accepted().body(response);
    }

   
    //지점 수정
    @PutMapping("/{storeUid}")
    public  ResponseEntity<StoreResponseDTO> updateStore(@PathVariable(name="storeUid") Long storeUid,
                                         @Valid @RequestBody StoreRequestDTO storeRequestDTO) throws StoreAlreadyExistsException, JsonProcessingException {
        StoreResponseDTO response = storeService.updateStore(storeUid,storeRequestDTO);
        return ResponseEntity.accepted().body(response);
    }
    //지점 삭제
    @DeleteMapping("/{storeUid}")
    public ResponseEntity<Void> deleteStore(@PathVariable("storeUid") Long storeUid) throws JsonProcessingException {
        storeService.deleteStore(storeUid);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/orders/{action}")
    public RabbitResponseDTO remoteOrder(@PathVariable(name = "action") String action, @RequestBody OrderCreatedMessage message) {

        return storeService.remoteOrderInQueue(action, message);
    }
}
