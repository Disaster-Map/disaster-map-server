package com.disastermap.disastermapserver.disasterMap.repository;

import com.disastermap.disastermapserver.disasterMap.domain.DisasterMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DisasterMessageRepository extends JpaRepository<DisasterMessage, Long> {
    // 가장 최근에 생성된 메시지 1개를 찾아서 반환하는 쿼리 메서드
    // crtDt 필드를 기준으로 내림차순(Desc) 정렬하여 첫 번째(Top) 항목을 찾습니다.
    Optional<DisasterMessage> findTopByOrderByCrtDtDesc();

    // 중복 확인용 메서드 (sn 필드를 기반으로)
    boolean existsBySn(Long sn);
}
