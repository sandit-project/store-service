package com.example.storeservice.domain;

import com.example.storeservice.dto.StoreResponseDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "store")
public class Store {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "uid")
        private Long storeUid;

        @NotBlank(message = "the store name must be defined.")
        @Column(name = "store_name")
        private String storeName;

        @Column(name = "user_uid")
        private Long userUid;

        @Column(name = "social_uid")
        private Long socialUid;

        @NotBlank(message = "the address must be defined.")
        @Column(name = "address")
        private String storeAddress;

        @NotNull(message = "the postcode must be defined.")
        @Column(name = "postcode")
        private String storePostcode;

        @NotNull(message = "the latitude must be defined.")
        @Column(name = "latitude")
        private Double storeLatitude;

        @NotNull(message = "the longitude must be defined.")
        @Column(name = "longitude")
        private Double storeLongitude;

        @NotBlank(message = "the status must be defined.")
        @Column(name = "status")
        private String storeStatus;

        @CreatedDate
        @Column(name = "created_date", updatable = false)
        private LocalDateTime storeCreatedDate;

        @Version
        private int version;

        // Store -> StoreResponseDTO 변환 메서드
        public StoreResponseDTO toStoreResponseDTO () {
                return StoreResponseDTO.builder()
                        .storeUid(this.getStoreUid())
                        .storeName(this.getStoreName())
                        .userUid(this.getUserUid())
                        .socialUid(this.getSocialUid())
                        .storeAddress(this.getStoreAddress())
                        .storePostcode(this.getStorePostcode())
                        .storeLatitude(this.getStoreLatitude())
                        .storeLongitude(this.getStoreLongitude())
                        .storeStatus(this.getStoreStatus())
                        .storeCreatedDate(this.getStoreCreatedDate())
                        .build();

        }
}