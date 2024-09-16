package com.example.consumer.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
    private static ObjectMapper mapper = new ObjectMapper();

    public static <T> T parseJson(String json, Class<T> valueType){
        T object;
        try {
            object = mapper.readValue(json,valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return object;
    }

    public static String stringifyJson(Object object){
        String json;
        try {
            json = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }
}
