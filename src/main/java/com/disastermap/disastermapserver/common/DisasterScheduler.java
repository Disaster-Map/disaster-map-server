package com.disastermap.disastermapserver.common;

import com.disastermap.disastermapserver.disasterMap.domain.DisasterMessage;
import com.disastermap.disastermapserver.disasterMap.dto.DisasterApiResponse;
import com.disastermap.disastermapserver.disasterMap.dto.DisasterMessageDto;
import com.disastermap.disastermapserver.disasterMap.service.DisasterMessageService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DisasterScheduler {

    private final RestTemplate restTemplate;
    private final DisasterMessageService disasterMessageService;

    private String API_KEY = "21GC1XPN5JGQO1GT";
    private int NUM_OF_ROWS_PER_PAGE = 1000;
    private String NUM_OF_ROWS_STR = String.valueOf(NUM_OF_ROWS_PER_PAGE);

    // YYYYMMDD 형식의 날짜 포맷터
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public DisasterScheduler(RestTemplate restTemplate, DisasterMessageService disasterMessageService) {
        this.restTemplate = restTemplate;
        this.disasterMessageService = disasterMessageService;
    }

    @Scheduled(fixedRate = 90000) // 90초마다 실행
    public void fetchNewDisasterMessages() {
        System.out.println("90초마다 새로운 재난 메시지 가져오기 시작...");

        // 1. DB에서 가장 최근에 저장된 메시지 정보 가져오기
        Optional<DisasterMessage> latestMessageOptional = disasterMessageService.findLatestMessage();
        String crtDt = "20250101"; // 기본값 (DB에 아무것도 없을 때)
        if (latestMessageOptional.isPresent()) {
            DisasterMessage latestMessage = latestMessageOptional.get();
            // 가장 최근 메시지의 생성 날짜를 기준으로 API 호출
            crtDt = latestMessage.getCrtDt().format(DATE_FORMATTER);
        }

        // 2. API 호출
        String apiUrl = UriComponentsBuilder.fromHttpUrl("https://www.safetydata.go.kr/V2/api/DSSP-IF-00247")
                .queryParam("serviceKey", API_KEY)
                .queryParam("crtDt", crtDt)
                .queryParam("numOfRows", NUM_OF_ROWS_STR)
                .queryParam("pageNo", "1") // 스케줄링 시에는 최신 데이터만 가져오므로 1페이지만 호출해도 충분합니다.
                .queryParam("_type", "json")
                .build()
                .toUriString();

        DisasterApiResponse apiResponse = restTemplate.getForObject(apiUrl, DisasterApiResponse.class);
        List<DisasterMessageDto> newMessages = new ArrayList<>();

        if (apiResponse != null && apiResponse.getBody() != null) {
            System.out.println("API 응답 헤더: " + apiResponse.getHeader().getResultMsg() + ", 코드: " + apiResponse.getHeader().getResultCode());
            System.out.println("총 " + apiResponse.getTotalCount() + "개의 데이터를 조회했습니다.");

            // 3. 중복되지 않은 데이터만 필터링
            for (DisasterMessageDto messageDto : apiResponse.getBody()) {
                if (!disasterMessageService.existsBySn(messageDto.getSn())) {
                    newMessages.add(messageDto);
                }
            }

            // 4. 새로운 데이터가 있으면 DB에 저장
            if (!newMessages.isEmpty()) {
                List<DisasterMessage> messagesToSave = new ArrayList<>();
                for (DisasterMessageDto dto : newMessages) {
                    messagesToSave.add(mapDtoToEntity(dto));
                }
                disasterMessageService.saveAll(messagesToSave);
                System.out.println(newMessages.size() + "개의 새로운 재난 메시지를 DB에 저장했습니다.");
            } else {
                System.out.println("새로운 재난 메시지가 없습니다.");
            }
        } else {
            System.out.println("API 응답이 null (= 재난 문자가 없음) 이거나 body가 비어있습니다.");
        }
    }

    // DTO를 Entity로 매핑하는 헬퍼 메서드
    private DisasterMessage mapDtoToEntity(DisasterMessageDto messageDto) {
        return DisasterMessage.builder()
                .sn(messageDto.getSn())
                .msgCn(messageDto.getMsgCn())
                .crtDt(messageDto.getCrtDt())
                .rcptnRgnNm(messageDto.getRcptnRgnNm())
                .emrgStepNm(messageDto.getEmrgStepNm())
                .dstSeNm(messageDto.getDstSeNm())
                .build();
    }
}
