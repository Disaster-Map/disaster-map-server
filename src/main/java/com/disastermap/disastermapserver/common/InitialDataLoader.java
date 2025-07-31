package com.disastermap.disastermapserver.common;

import com.disastermap.disastermapserver.disasterMap.domain.DisasterMessage;
import com.disastermap.disastermapserver.disasterMap.dto.DisasterApiResponse;
import com.disastermap.disastermapserver.disasterMap.dto.DisasterMessageDto;
import com.disastermap.disastermapserver.disasterMap.repository.DisasterMessageRepository;
import com.disastermap.disastermapserver.disasterMap.service.DisasterMessageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Component
public class InitialDataLoader implements CommandLineRunner {
    private final RestTemplate restTemplate;
    private final DisasterMessageService disasterMessageService; // Repository 대신 Service를 주입받는 것이 좋지만, 현재 코드에서는 Repository를 사용하므로 유지
    // private final DisasterMessageService disasterMessageService; // 서비스 계층을 통해 저장한다면 이 주석을 해제하고, repository 대신 사용

    private String API_KEY = "21GC1XPN5JGQO1GT";
    private String CRT_DT = "20250101"; // 조회 시작 일자
    private int NUM_OF_ROWS_PER_PAGE = 1000; // int 형으로 변경하여 계산에 용이
    private String NUM_OF_ROWS_STR = String.valueOf(NUM_OF_ROWS_PER_PAGE); // API 호출용 String

    // 저장할 메시지들을 담을 리스트
    List<DisasterMessage> messagesToSave = new ArrayList<>();

    public InitialDataLoader(RestTemplate restTemplate, DisasterMessageService disasterMessageService /*, DisasterMessageService disasterMessageService*/) {
        this.restTemplate = restTemplate;
        this.disasterMessageService = disasterMessageService;
        // this.disasterMessageService = disasterMessageService;
    }

    @Override
    public void run(String... args) throws Exception {

        int totalCount = 0;
        int totalPages = 0;

        // 1. 첫 페이지를 호출하여 totalCount를 가져오고, 총 페이지 수를 계산합니다.
        String initialApiUrl = UriComponentsBuilder.fromHttpUrl("https://www.safetydata.go.kr/V2/api/DSSP-IF-00247")
                .queryParam("serviceKey", API_KEY)
                .queryParam("crtDt", CRT_DT)
                .queryParam("numOfRows", NUM_OF_ROWS_STR) // 첫 호출도 1000개 요청
                .queryParam("pageNo", "1") // 첫 페이지를 호출
                .queryParam("_type", "json")
                .build()
                .toUriString();

        DisasterApiResponse initialResponse = restTemplate.getForObject(initialApiUrl, DisasterApiResponse.class);

        if (initialResponse != null && initialResponse.getTotalCount() != null) {
            totalCount = initialResponse.getTotalCount(); // 전체 데이터 개수
            totalPages = (int) Math.ceil((double) totalCount / NUM_OF_ROWS_PER_PAGE);

            System.out.println("API에 총 데이터 개수: " + totalCount + ", 계산된 총 페이지 수: " + totalPages);

            // 2. 첫 페이지에서 가져온 데이터도 리스트에 추가합니다.
            if (initialResponse.getBody() != null) {
                for (DisasterMessageDto message : initialResponse.getBody()) {
                    messagesToSave.add(mapDtoToEntity(message));
                }
            }

            // 3. 나머지 페이지 (2페이지부터 totalPages까지)를 순회하며 데이터를 가져옵니다.
            for (int i = 2; i <= totalPages; i++) { // 루프 조건 변경: totalPages까지 포함
                String currentPageNo = String.valueOf(i);

                String apiUrl = UriComponentsBuilder.fromHttpUrl("https://www.safetydata.go.kr/V2/api/DSSP-IF-00247")
                        .queryParam("serviceKey", API_KEY)
                        .queryParam("crtDt", CRT_DT)
                        .queryParam("numOfRows", NUM_OF_ROWS_STR) // 모든 페이지에 대해 numOfRows는 1000으로 유지
                        .queryParam("pageNo", currentPageNo)
                        .queryParam("_type", "json")
                        .build()
                        .toUriString();

                DisasterApiResponse apiResponse = restTemplate.getForObject(apiUrl, DisasterApiResponse.class);

                if (apiResponse != null && apiResponse.getBody() != null) {
                    List<DisasterMessageDto> messages = apiResponse.getBody();
                    for (DisasterMessageDto message : messages) {
                        messagesToSave.add(mapDtoToEntity(message));
                    }
                } else {
                    System.out.println("페이지 " + i + ": API 응답이 null이거나 body가 비어있습니다. (API 호출 중단)");
                    // API 응답이 비어있는 경우 더 이상 페이지가 없다고 판단하고 루프를 종료할 수도 있습니다.
                    break;
                }
                // API 서버 부하를 줄이기 위해 페이지 호출 간에 잠시 딜레이를 주는 것을 고려할 수 있습니다.
                // Thread.sleep(100); // 예: 0.1초 딜레이
            }
        } else {
            System.out.println("초기 API 호출 실패 또는 totalCount를 가져올 수 없습니다.");
        }


        // 모든 페이지의 데이터를 모은 후, 한 번에 저장합니다.
        if (!messagesToSave.isEmpty()) {
            System.out.println("총 " + messagesToSave.size() + "개의 재난 메시지를 배치로 저장합니다.");
            // 서비스 계층을 통해 saveAll 호출하는 것이 트랜잭션 관리에 더 적합합니다.
            // disasterMessageService.saveAll(messagesToSave);
            disasterMessageService.saveAll(messagesToSave); // 현재 코드에서는 Repository 직접 호출
            System.out.println("배치 저장 완료.");
        } else {
            System.out.println("저장할 재난 메시지가 없습니다.");
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