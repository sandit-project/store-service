package com.example.storeservice.controller;

import com.example.storeservice.dto.StoreListResponseDTO;
import com.example.storeservice.dto.StoreRequestDTO;
import com.example.storeservice.dto.StoreResponseDTO;
import com.example.storeservice.exception.StoreAlreadyExistsException;
import com.example.storeservice.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
    @GetMapping("/{uid}")
    public StoreResponseDTO getStore(@PathVariable(name="uid") Long uid) {
        return storeService.viewStore(uid);
    }

    //지점 추가
    @PostMapping
    public StoreResponseDTO addStore(@Valid @RequestBody  StoreRequestDTO storeRequestDTO) throws StoreAlreadyExistsException, IOException {
        return storeService.addStore(storeRequestDTO);
    }
    //지점 수정
    @PutMapping("/{uid}")
    public  ResponseEntity<StoreResponseDTO> updateStore(@PathVariable(name="uid") Long uid,
                                         @Valid @RequestBody StoreRequestDTO storeRequestDTO) throws StoreAlreadyExistsException {
        StoreResponseDTO response = storeService.updateStore(uid,storeRequestDTO);
        return ResponseEntity.ok(response);
    }
    //지점 삭제
    @DeleteMapping("/{uid}")
    public void deleteStore(@PathVariable("uid") Long uid) {
        storeService.deleteStore(uid);
    }

    //지점 상태 업데이트
    @PatchMapping("/{uid}")
    public void updateStatusByUid(@PathVariable("uid") Long uid, @RequestParam("storeStatus") String storeStatus) {
        storeService.updateStatusStore(uid, storeStatus);
    }

}
