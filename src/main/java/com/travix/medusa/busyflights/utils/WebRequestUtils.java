package com.travix.medusa.busyflights.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

public class WebRequestUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private WebRequestUtils() {
    }

    public static <T> MultiValueMap<String, String> toMultiValueMap(T obj) {
        MultiValueMap<String, String> valueMap = new LinkedMultiValueMap<>();
        Map<String, String> fieldMap = objectMapper.convertValue(obj, new TypeReference<Map<String, String>>() {});
        valueMap.setAll(fieldMap);
        return valueMap;
    }
}
