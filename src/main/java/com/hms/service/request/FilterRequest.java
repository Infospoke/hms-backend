package com.hms.service.request;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FilterRequest {

    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "id";
    private String direction; 

    private Map<String, Object> filters;
}