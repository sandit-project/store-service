package com.example.storeservice.service;

import com.example.storeservice.dto.StoreOrderDetailsDTO;
import com.example.storeservice.dto.StoreOrderListResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
// @Profile({"dev", "test"})  // 개발·테스트 환경에서만 활성화하려면
@RequiredArgsConstructor
public class StoreOrderDetailsServiceImpl implements StoreOrderDetailsService {

    @Override
    public StoreOrderDetailsDTO getSampleOrderDetail(Integer orderUid) {
        // DTO.sample() 내부에서 임의의 uid를 세팅하므로,
        // orderUid를 DTO에 반영하고 싶다면 Builder에서 덮어쓰시면 됩니다.
        return StoreOrderDetailsDTO.sampleData1()
                .toBuilder()
                .uid(orderUid)    // PathVariable 등으로 받은 값을 반영
                .build();
    }
    //지점 주문 목록 조회(커서방식)
    @Override
    @Transactional
    public StoreOrderListResponseDTO getStoreOrdersByCurcor(int limit, Long lastUid) {
        // (1) 샘플 DTO 두 개를 준비
        StoreOrderDetailsDTO dto1 = StoreOrderDetailsDTO.sampleData1()
                .toBuilder().uid(101).build();
        StoreOrderDetailsDTO dto2 = StoreOrderDetailsDTO.sampleData2()
                .toBuilder().uid(102).build();

        // (2) 리스팅: 실제는 DB 호출 → 테스트용은 List.of(dto1,dto2...)
        List<StoreOrderDetailsDTO> list = List.of(dto1, dto2);

        // (3) 커서·마지막 페이지 플래그 결정
        Long nextCursor = list.size() == limit
                ? list.get(list.size() - 1).getUid().longValue()
                : null;
        boolean lastPage = list.size() < limit;

        // (4) 빌드 후 반환
        return StoreOrderListResponseDTO.builder()
                .storeOrderList(list)
                .nextCursor(nextCursor)
                .lastPage(lastPage)
                .build();
    }

}
