package com.example.storeservice.service;

import com.example.storeservice.domain.Store;
import com.example.storeservice.dto.StoreListResponseDTO;
import com.example.storeservice.dto.StoreRequestDTO;
import com.example.storeservice.dto.StoreResponseDTO;
import com.example.storeservice.exception.StoreAlreadyExistsException;
import com.example.storeservice.exception.StoreNotFoundException;
import com.example.storeservice.repository.StoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static com.example.storeservice.domain.Store.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private StoreService storeService;

    // --- getStoresByCursor ---

    @Test
    void getStoresByCursor_firstPage_returnsListAndNextCursor() {
        // given
        int limit = 2;
        Store s1 = builder().uid(1L).storeName("A점").build();
        Store s2 = builder().uid(2L).storeName("B점").build();
        given(storeRepository.findFirstByOrderByUidAsc(limit))
                .willReturn(Arrays.asList(s1, s2));

        // when
        StoreListResponseDTO result = storeService.getStoresByCursor(limit, null);

        // then
        assertThat(result.getStoreList()).hasSize(2);
        assertThat(result.isLastPage()).isFalse();   // size < limit? here size == limit → false, 하지만 예시데이터 따라 조정
        assertThat(result.getNextCursor()).isEqualTo(2L);
        then(storeRepository).should().findFirstByOrderByUidAsc(limit);
    }

    @Test
    void getStoresByCursor_afterCursor_returnsSubset() {
        // given
        int limit = 3;
        long lastUid = 5L;
        Store s3 = builder().uid(6L).storeName("C점").build();
        given(storeRepository.findByUidGreaterThanOrderByUidAsc(lastUid, limit))
                .willReturn(Collections.singletonList(s3));

        // when
        StoreListResponseDTO dto = storeService.getStoresByCursor(limit, lastUid);

        // then
        assertThat(dto.getStoreList()).extracting("uid").containsExactly(6L);
        assertThat(dto.isLastPage()).isTrue();     // list size(1) < limit(3)
        assertThat(dto.getNextCursor()).isEqualTo(6L);
        then(storeRepository).should().findByUidGreaterThanOrderByUidAsc(lastUid, limit);
    }

    // --- viewStore ---

    @Test
    void viewStore_existingUid_returnsDTO() {
        // given
        Store store = builder()
                .uid(10L)
                .storeName("테스트점")
                .build();
        given(storeRepository.findByUid(10L)).willReturn(Optional.of(store));

        // when
        StoreResponseDTO dto = storeService.viewStore(10L);

        // then
        assertThat(dto.getUid()).isEqualTo(10L);
        assertThat(dto.getStoreName()).isEqualTo("테스트점");
        then(storeRepository).should().findByUid(10L);
    }

    @Test
    void viewStore_notFound_throws() {
        // given
        given(storeRepository.findByUid(anyLong())).willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> storeService.viewStore(99L))
                .isInstanceOf(StoreNotFoundException.class)
                .hasMessageContaining("99");
    }

    // --- addStore ---

    @Test
    void addStore_newName_savesAndReturnsDTO() throws Exception, StoreAlreadyExistsException {
        // given
        StoreRequestDTO req = StoreRequestDTO.builder()
                .storeName("신규점")
                .storeAddress("주소")
                .storePostcode("12345")
                .storeLatitude(37.1)
                .storeLongitude(127.1)
                .storeStatus("OPEN")
                .build();

        given(storeRepository.existsByStoreName("신규점")).willReturn(false);

        LocalDateTime now = LocalDateTime.now();

        Store saved = builder()
                .uid(100L)
                .storeName("신규점")
                .storeAddress("주소")
                .storePostcode("12345")
                .storeLatitude(37.1)
                .storeLongitude(127.1)
                .storeStatus("OPEN")
                .storeCreatedDate(now)
                .version(0)
                .build();
        given(storeRepository.save(any(Store.class))).willReturn(saved);

        // when
        StoreResponseDTO dto = storeService.addStore(req);

        // then
        assertThat(dto.getUid()).isEqualTo(100L);
        assertThat(dto.getStoreName()).isEqualTo("신규점");
        then(storeRepository).should().save(any(Store.class));
    }

    @Test
    void addStore_duplicateName_throws() {
        // given
        given(storeRepository.existsByStoreName("중복점")).willReturn(true);
        StoreRequestDTO req = StoreRequestDTO.builder()
                .storeName("중복점").build();

        // when / then
        assertThatThrownBy(() -> storeService.addStore(req))
                .isInstanceOf(StoreAlreadyExistsException.class)
                .hasMessageContaining("중복점");
    }

    // --- updateStore ---

    @Test
    void updateStore_existing_updatesAndReturnsDTO() throws Exception {
        // given
        long uid = 5L;
        StoreRequestDTO req = StoreRequestDTO.builder()
                .storeName("수정점")
                .storeAddress("새주소")
                .storePostcode("54321")
                .storeLatitude(37.2)
                .storeLongitude(127.2)
                .storeStatus("CLOSED")
                .build();

        LocalDateTime now = LocalDateTime.now();

        Store existing = builder()
                .uid(uid)
                .storeCreatedDate(now)
                .version(1)
                .build();
        given(storeRepository.findByUid(uid)).willReturn(Optional.of(existing));

        Store updated = builder()
                .uid(uid)
                .storeName("수정점")
                .storeAddress("새주소")
                .storePostcode("54321")
                .storeLatitude(37.2)
                .storeLongitude(127.2)
                .storeStatus("CLOSED")
                .storeCreatedDate(existing.getStoreCreatedDate())
                .version(1)
                .build();
        given(storeRepository.save(any(Store.class))).willReturn(updated);

        // when
        StoreResponseDTO dto = storeService.updateStore(uid, req);

        // then
        assertThat(dto.getStoreName()).isEqualTo("수정점");
        then(storeRepository).should().findByUid(uid);
        then(storeRepository).should().save(any(Store.class));
    }

    @Test
    void updateStore_notFound_throws() {
        // given
        given(storeRepository.findByUid(anyLong())).willReturn(Optional.empty());
        StoreRequestDTO req = StoreRequestDTO.builder().build();

        // when / then
        assertThatThrownBy(() -> storeService.updateStore(999L, req))
                .isInstanceOf(StoreNotFoundException.class);
    }

    // --- updateStatusStore ---

    @Test
    void updateStatusStore_existing_callsRepository() {
        // given
        long uid = 7L;
        given(storeRepository.existsById(uid)).willReturn(true);

        // when
        storeService.updateStatusStore(uid, "SUSPENDED");

        // then
        then(storeRepository).should().updateStatusByUid(uid, "SUSPENDED");
    }

    @Test
    void updateStatusStore_notFound_throws() {
        // given
        given(storeRepository.existsById(anyLong())).willReturn(false);

        // when / then
        assertThatThrownBy(() -> storeService.updateStatusStore(42L, "X"))
                .isInstanceOf(StoreNotFoundException.class);
    }

    // --- deleteStore ---

    @Test
    void deleteStore_existing_callsRepository() {
        // given
        long uid = 8L;
        given(storeRepository.existsById(uid)).willReturn(true);

        // when
        storeService.deleteStore(uid);

        // then
        then(storeRepository).should().deleteByUid(uid);
    }

    @Test
    void deleteStore_notFound_throws() {
        // given
        given(storeRepository.existsById(anyLong())).willReturn(false);

        // when / then
        assertThatThrownBy(() -> storeService.deleteStore(123L))
                .isInstanceOf(StoreNotFoundException.class);
    }
}
