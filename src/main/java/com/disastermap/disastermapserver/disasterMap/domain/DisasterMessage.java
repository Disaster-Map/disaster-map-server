package com.disastermap.disastermapserver.disasterMap.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class DisasterMessage {

    @Id // SN이 각 재난문자의 고유 식별자로 보입니다.
    private Long sn; // SN (일련번호) 필드를 ID로 사용

    @Column(columnDefinition = "TEXT")
    private String msgCn; // 메시지 내용

    @Column(columnDefinition = "TEXT")
    private String rcptnRgnNm; // 수신 지역명

    // 날짜/시간 필드. 문자열로 그대로 저장하거나, LocalDateTime으로 변환하여 저장할 수 있습니다.
    // 여기서는 일단 JSON 포맷 그대로 String으로 저장합니다.
    private LocalDateTime crtDt; // 생성 일시 (YYYY/MM/DD HH:MM:SS)

    private String emrgStepNm; // 긴급 단계명
    private String dstSeNm; // 재난 구분명
}
