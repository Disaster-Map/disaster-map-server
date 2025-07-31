package com.disastermap.disastermapserver.disasterMap.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisasterMessageDto {
    @JsonProperty("SN") // JSON 필드명 "SN"을 sn 필드에 매핑 // 일련 번호
    private Long sn;

    @JsonProperty("MSG_CN") // JSON 필드명 "MSG_CN"을 msgCn 필드에 매핑 // 메시지 내용
    private String msgCn;

    @JsonProperty("RCPTN_RGN_NM") // JSON 필드명 "RCPTN_RGN_NM"을 rcptnRgnNm 필드에 매핑 //  수신 지역 명
    private String rcptnRgnNm;

    @JsonProperty("CRT_DT") // JSON 필드명 "CRT_DT"를 crtDt 필드에 매핑 // 생성 일시
    // JSON 문자열 "2025/01/06 18:41:19"을 LocalDateTime으로 파싱하기 위한 형식 지정 
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime crtDt;

    @JsonProperty("EMRG_STEP_NM") // JSON 필드명 "EMRG_STEP_NM"을 emrgStepNm 필드에 매핑 // 긴급 단계 명
    private String emrgStepNm;

    @JsonProperty("DST_SE_NM") // JSON 필드명 "DST_SE_NM"을 dstSeNm 필드에 매핑
    private String dstSeNm;
}
