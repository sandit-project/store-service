package com.example.storeservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
@Getter
@Builder
public class StoreOrderListResponseDTO {
    private List<StoreOrderDetailsDTO> storeOrderList;  // 지점 주문 리스트
    private boolean lastPage;  // 마지막 페이지 여부
    private Long nextCursor;  // 다음 페이지를 위한 커서 (lastUid 또는 lastCreatedDate)
}
