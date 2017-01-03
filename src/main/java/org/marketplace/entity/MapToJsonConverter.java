package org.marketplace.entity;

import java.io.IOException;
import java.util.Map;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import javax.persistence.PersistenceException;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Converter
public class MapToJsonConverter implements AttributeConverter<Map<String, Object>, String> {

    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> details) {
        if (details == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(details);
        } catch (final IOException e) {
            throw new PersistenceException("Failed to convert map to JSON string.", e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String details) {
        if (details == null || details.length() == 0) {
            return null;
        }

        try {
            return OBJECT_MAPPER.readValue(details, new TypeReference<Map<String, Object>>() {
            });
        } catch (final IOException e) {
            throw new PersistenceException("Failed to convert JSON string to map.", e);
        }
    }

}