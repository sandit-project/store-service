package com.example.storeservice.service;

import com.example.storeservice.domain.Store;
import com.example.storeservice.dto.StoreListResponseDTO;
import com.example.storeservice.dto.StoreRequestDTO;
import com.example.storeservice.dto.StoreResponseDTO;
import com.example.storeservice.exception.StoreAlreadyExistsException;
import com.example.storeservice.exception.StoreNotFoundException;
import com.example.storeservice.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;


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
        Long nextCursor = stores.isEmpty() ? null : stores.get(stores.size() - 1).getUid();


        return StoreListResponseDTO.builder()
                .storeList(storeResponseDTOS)
                .lastPage(lastPage)
                .nextCursor(nextCursor)
                .build();


    }

    // 지점 이름으로 지점 조회
    public StoreResponseDTO viewStore(Long uid) {
        Store store = storeRepository.findByUid(uid)
                .orElseThrow(() -> new StoreNotFoundException(uid));
        return store.toStoreResponseDTO();
    }

    //지점 추가
    public StoreResponseDTO addStore(StoreRequestDTO storeRequestDTO) throws StoreAlreadyExistsException, IOException {
        if (storeRepository.existsByStoreName(storeRequestDTO.getStoreName())) {
            throw new StoreAlreadyExistsException(storeRequestDTO.getStoreName());
        }

        Store store = Store.builder()
                .storeName(storeRequestDTO.getStoreName())
                .storeAddress(storeRequestDTO.getStoreAddress())
                .storePostcode(storeRequestDTO.getStorePostcode())
                .storeLatitude(storeRequestDTO.getStoreLatitude())
                .storeLongitude(storeRequestDTO.getStoreLongitude())
                .storeStatus(storeRequestDTO.getStoreStatus())
                .build();

        Store saveStore = storeRepository.save(store);//DB에 저장해 줌.
        return saveStore.toStoreResponseDTO();
    }

    //지점 수정
    @Transactional
    public StoreResponseDTO updateStore(Long uid, StoreRequestDTO storeRequestDTO) throws StoreNotFoundException {
        Store existingStore = storeRepository.findByUid(uid)
                .orElseThrow(() -> new StoreNotFoundException(uid));

        Store updateStore = Store.builder()
                .uid(existingStore.getUid())
                .storeName(storeRequestDTO.getStoreName())
                .storeAddress(storeRequestDTO.getStoreAddress())
                .storePostcode(storeRequestDTO.getStorePostcode())
                .storeLatitude(storeRequestDTO.getStoreLatitude())
                .storeLongitude(storeRequestDTO.getStoreLongitude())
                .storeStatus(storeRequestDTO.getStoreStatus())
                .storeCreatedDate(existingStore.getStoreCreatedDate())
                .version(existingStore.getVersion())//버전 증가
                .build();
        Store saveStore = storeRepository.save(updateStore);
        return saveStore.toStoreResponseDTO();

    }

    //지점 상태 변경
    public void updateStatusStore (Long uid, String status) throws StoreNotFoundException {
        if (!storeRepository.existsById(uid)) {
            throw new StoreNotFoundException(uid);
        }
        storeRepository.updateStatusByUid(uid, status);
    }

    //지점 삭제
    public void deleteStore (Long uid){
        if (!storeRepository.existsById(uid)) {
            throw new StoreNotFoundException(uid);
        }
        storeRepository.deleteByUid(uid);
    }
}


