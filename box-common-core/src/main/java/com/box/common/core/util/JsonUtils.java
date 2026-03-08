package com.box.common.core.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * JSON 工具。
 */
public final class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

    private JsonUtils() {
    }

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    public static String toJson(Object value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to write json", exception);
        }
    }

    public static String toPrettyJson(Object value) {
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to write pretty json", exception);
        }
    }

    public static <T> T parseObject(String json, Class<T> targetClass) {
        try {
            return OBJECT_MAPPER.readValue(json, targetClass);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to parse json", exception);
        }
    }

    public static <T> T parseObject(String json, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to parse json", exception);
        }
    }

    public static <T> List<T> parseList(String json, Class<T> elementType) {
        try {
            JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, elementType);
            return OBJECT_MAPPER.readValue(json, javaType);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to parse json list", exception);
        }
    }

    public static Map<String, Object> toMap(Object value) {
        if (value == null) {
            return Collections.emptyMap();
        }
        return OBJECT_MAPPER.convertValue(value, new TypeReference<>() {
        });
    }

    public static <T> T convert(Object source, Class<T> targetClass) {
        return OBJECT_MAPPER.convertValue(source, targetClass);
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return objectMapper;
    }
}
