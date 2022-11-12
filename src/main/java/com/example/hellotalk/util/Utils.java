package com.example.hellotalk.util;

import com.example.hellotalk.exception.JacksonConversionException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
    
    public static String jsonStringFromObject(Object jsonObject) {
        if (jsonObject == null) {
            return "";
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String jsonStr;
        try {
            jsonStr = mapper.writeValueAsString(jsonObject);
        } catch (JsonProcessingException e) {
            throw new JacksonConversionException(e.getMessage());
        }
        return jsonStr;
    }

    public static <T> T objectFromJsonString(String json, Class<T> c) {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructType(c));
        } catch (JsonProcessingException e) {
            throw new JacksonConversionException(e.getMessage());
        }
    }
    
    public static <T> T convertToNewObject(Object o, Class<T> c) {
        String json = jsonStringFromObject(o);
        return objectFromJsonString(json, c);
    }
    
}
