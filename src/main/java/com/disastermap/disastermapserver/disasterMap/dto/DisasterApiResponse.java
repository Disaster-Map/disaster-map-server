package com.disastermap.disastermapserver.disasterMap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisasterApiResponse {
    @JsonProperty("header")
    private Header header;

    @JsonProperty("numOfRows")
    private Integer numOfRows;

    @JsonProperty("pageNo")
    private Integer pageNo;

    @JsonProperty("totalCount")
    private Integer totalCount;

    @JsonProperty("body")
    private List<DisasterMessageDto> body;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Header {
        @JsonProperty("resultMsg")
        private String resultMsg;
        @JsonProperty("resultCode")
        private String resultCode;
        @JsonProperty("errorMsg")
        private String errorMsg;
    }
}
