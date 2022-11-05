package com.example.hellotalk.util;

import com.example.hellotalk.model.user.User;
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
            throw new RuntimeException(e.getMessage()); // NOSONAR
        }
        return jsonStr;
    }

    public static User objectFromJsonString(String json) {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, User.class); // todo: make it generic
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Object convertToNewObject(User user) { //todo: make it generic
        String json = jsonStringFromObject(user);
        return Utils.objectFromJsonString(json);
    }
}
