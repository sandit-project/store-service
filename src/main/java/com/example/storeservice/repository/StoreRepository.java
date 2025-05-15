package com.example.storeservice.repository;

import com.example.storeservice.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    //지점 uid로 지점을 조회하는 메서드.JPA가 메서드 이름을 보고 자동으로 쿼리 생성해 줌.
    Optional<Store> findByStoreUid(Long storeUid);

    //매니저 UID로 지점 조회
    Optional<Store> findByUserUid(Long userUid);

    // 지점 상태를 업데이트하는 메서드 (예시: 상태 변경)
    @Modifying
    @Transactional
    @Query("UPDATE Store s SET s.storeStatus = :storeStatus WHERE s.storeUid= :uid")
    void updateStatusByUid(@Param("storeUid") Long storeUid, @Param("storeStatus") String storeStatus);

    // 지점 이름이 존재하는지 확인하는 메서드
    boolean existsByStoreName(String storeName);

    // 처음 페이지 조회:UID오른차순 정렬 후 limit 개수만큰 조회
    @Query(value = "SELECT ROW_NUMBER() OVER (ORDER BY uid) AS row_num,uid,store_name,user_uid,social_uid,address,postcode,status,created_date,version,latitude,longitude FROM store ORDER BY uid ASC LIMIT :limit", nativeQuery = true)
    List<Store>  findFirstByOrderByUidAsc(@Param("limit") int limit);

    // 이후 페이지 조회:주어진 UID 이후 데이터 LIMIT 개수만 조회
    @Query(value = "SELECT ROW_NUMBER() OVER (ORDER BY uid) AS row_num,uid,store_name,user_uid,social_uid,address,postcode,status,created_date,version,latitude,longitude FROM store WHERE uid > :lastUid ORDER BY uid ASC LIMIT :limit", nativeQuery = true)
    List<Store>  findByUidGreaterThanOrderByUidAsc(@Param("lastUid") Long lastUid,@Param("limit") int limit) ;


}
