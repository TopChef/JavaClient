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
    /**
     *
     * @param JSON The JSON object to deserialize as a string
     * @return The JSON object as a {@link Object}
     * @throws IOException If the data cannot be read
     * @throws JsonParseException If the incoming JSON cannot be parsed
     */
    Object readValue(String JSON) throws IOException, JsonParseException;

    /**
     *
     * @param JSON The JSON object to deserialize as a string
     * @param typeOfJSON The class to which this object is to be deserialized
     * @param <T> The type of object that this method should return
     * @return An object of the required type, built from the given JSON
     * @throws IOException If the data cannot be read
     * @throws JsonParseException If syntactically-incorrect JSON was sent to this device
     * @throws JsonMappingException I
     */
    <T> T readValue(String JSON, Class<T> typeOfJSON) throws IOException, JsonParseException, JsonMappingException;

    /**
     *
     * @param stream A character stream from which JSON is to be read.
     * @param typeOfJSON The class to which the result is to be marshalled
     * @param <T> The type to which data is to be marshalled
     * @return The desired instance, built using data from this JSON
     * @throws IOException If the data could not be read
     * @throws JsonParseException If syntactically-incorrect JSON is sent in
     * @throws JsonMappingException If good JSON is sent in, but it doesn't map to a property of ``T``
     */
    <T> T readValue(InputStream stream, Class<T> typeOfJSON) throws IOException, JsonParseException,
            JsonMappingException;

    String writeValueAsString(Object value) throws JsonProcessingException;

    /**
     *
     * @param writer The {@link Writer} to which this data is to be written
     * @param value The object that needs to be expressed as JSON
     * @throws IOException If the underlying {@link Writer} throws an {@link IOException}
     * @throws JsonProcessingException If there is a problem processing the JSON
     */
    void writeValue(Writer writer, Object value) throws IOException;

    /**
     *
     * @param stream The {@link OutputStream} to which this data is to be written
     * @param value The object to be expressed as JSON
     * @throws IOException If the underlying {@link Writer} throws an {@link IOException}
     * @throws JsonProcessingException If there is a problem processing the JSON
     */
    void writeValue(OutputStream stream, Object value) throws IOException;
}
