package com.example.storeservice.controller;

import com.example.storeservice.dto.*;
import com.example.storeservice.exception.StoreAlreadyExistsException;
import com.example.storeservice.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stores")
public class StoreApiController {

    private final StoreService storeService;

    //지점 목록 조회
    @GetMapping("/list")
    public StoreListResponseDTO getAllStores(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Long lastUid
             ){
        // 지점 목록 가져오기
        return  storeService.getStoresByCursor(limit, lastUid);
    }

    //지점 uid로 지점 조회
    @GetMapping("/{storeUid}")
    public StoreResponseDTO getStore(@PathVariable(name="storeUid") Long storeUid) {
        return storeService.viewStore(storeUid);
    }

    /**
     * 매니저 UID 로 해당 지점 UID,storeName 반환
     * GET /stores/storeUid?managerUid=123
     */
    @GetMapping("/storeUid")
    public StoreUidResponseDTO getStoreUidByManagerUid(@RequestParam(name = "managerUid") Long managerUid) {
        Long storeUid = storeService.getStoreUidByManagerUid(managerUid);
        String storeName = storeService.viewStore(storeUid).getStoreName();
        return StoreUidResponseDTO.builder()
                .storeUid(storeUid)
                .storeName(storeName)
                .build();
    }

    //지점 추가
    @PostMapping
    public StoreResponseDTO addStore(@Valid @RequestBody  StoreRequestDTO storeRequestDTO) throws StoreAlreadyExistsException, IOException {
        return storeService.addStore(storeRequestDTO);
    }
    //지점 수정
    @PutMapping("/{storeUid}")
    public  ResponseEntity<StoreResponseDTO> updateStore(@PathVariable(name="storeUid") Long storeUid,
                                         @Valid @RequestBody StoreRequestDTO storeRequestDTO) throws StoreAlreadyExistsException {
        StoreResponseDTO response = storeService.updateStore(storeUid,storeRequestDTO);
        return ResponseEntity.ok(response);
    }
    //지점 삭제
    @DeleteMapping("/{storeUid}")
    public void deleteStore(@PathVariable("storeUid") Long storeUid) {
        storeService.deleteStore(storeUid);
    }

    //지점 상태 업데이트
    @PatchMapping("/{storeUid}")
    public void updateStatusByUid(@PathVariable("storeUid") Long storeUid, @RequestParam("storeStatus") String storeStatus) {
        storeService.updateStatusStore(storeUid, storeStatus);
    }

    @GetMapping("/orders/{action}")
    public RabbitResponseDTO remoteOrder(@PathVariable(name = "action") String action) {
        return storeService.remoteOrderInQueue(action);
    }
}
