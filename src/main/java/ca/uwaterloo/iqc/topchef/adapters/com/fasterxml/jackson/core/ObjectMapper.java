package ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

/**
 * Describes the interface to Jackson's JSON writer
 */
public interface ObjectMapper {
    Object readValue(String JSON) throws IOException, JsonParseException, JsonMappingException;

    <T> T readValue(String JSON, Class<T> typeOfJSON) throws IOException, JsonParseException, JsonMappingException;

    <T> T readValue(InputStream stream, Class<T> typeOfJSON) throws IOException, JsonParseException,
            JsonMappingException;

    String writeValueAsString(Object value) throws JsonProcessingException;

    void writeValue(Writer writer, Object value) throws IOException;

    void writeValue(OutputStream stream, Object value) throws IOException;
}
