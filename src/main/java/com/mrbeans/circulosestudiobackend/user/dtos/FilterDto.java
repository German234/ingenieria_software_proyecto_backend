package com.mrbeans.circulosestudiobackend.user.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FilterDto {

    @JsonProperty("lastActivityFromDate")
    private String lastActivityFromDate;

    @JsonProperty("lastActivityToDate")
    private String lastActivityToDate;
}
