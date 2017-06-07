package ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.wrapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Wraps {@link com.fasterxml.jackson.databind.ObjectMapper}
 */
public final class ObjectMapper implements ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.ObjectMapper {
    /**
     * The Jackson wrapper that this class wraps
     */
    private final com.fasterxml.jackson.databind.ObjectMapper wrappedMapper;

    /**
     * Create a new Jackson object mapper
     */
    public ObjectMapper(){
        this.wrappedMapper = new com.fasterxml.jackson.databind.ObjectMapper();
    }


    /**
     *
     * @param JSON The JSON object to deserialize as a string
     * @return The JSON object as a {@link Object}
     * @throws IOException If the data cannot be read
     * @throws JsonParseException If the incoming JSON cannot be parsed
     */
    @Override
    public Object readValue(String JSON) throws IOException, JsonParseException {
        return this.wrappedMapper.readValue(JSON, Object.class);
    }

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
    @Override
    public <T> T readValue(String JSON, Class<T> typeOfJSON) throws IOException,
            JsonParseException, JsonMappingException {
        return this.wrappedMapper.readValue(JSON.getBytes(), typeOfJSON);
    }

    /**
     *
     * This is the best method to read in JSON, as it lets the wrapped Jackson object mapper
     * figure out how to read the text in, optimizing memory and processor use
     *
     * @param stream A character stream from which JSON is to be read.
     * @param typeOfJSON The class to which the result is to be marshalled
     * @param <T> The type to which data is to be marshalled
     * @return The desired instance, built using data from this JSON
     * @throws IOException If the data could not be read
     * @throws JsonParseException If syntactically-incorrect JSON is sent in
     * @throws JsonMappingException If good JSON is sent in, but it doesn't map to a property of ``T``
     */
    @Override
    public <T> T readValue(InputStream stream, Class<T> typeOfJSON) throws IOException, JsonParseException, JsonMappingException {
        return this.wrappedMapper.readValue(stream, typeOfJSON);
    }

    /**
     *
     * @param value The object to write
     * @return The JSON string representing the object
     * @throws JsonProcessingException If the object cannot be processed
     */
    @Override
    public String writeValueAsString(Object value) throws JsonProcessingException {
        return this.wrappedMapper.writeValueAsString(value);
    }

    /**
     *
     * @param writer The {@link Writer} to which this data is to be written
     * @param value The object that needs to be expressed as JSON
     * @throws IOException If the underlying {@link Writer} throws an {@link IOException}
     * @throws JsonProcessingException If there is a problem processing the JSON
     */
    @Override
    public void writeValue(Writer writer, Object value) throws IOException, JsonProcessingException {
        this.wrappedMapper.writeValue(writer, value);
    }

    /**
     *
     * It would be best to use this method for writing JSON
     *
     * @param stream The {@link OutputStream} to which this data is to be written
     * @param value The object to be expressed as JSON
     * @throws IOException If the underlying {@link Writer} throws an {@link IOException}
     * @throws JsonProcessingException If there is a problem processing the JSON
     */
    @Override
    public void writeValue(OutputStream stream, Object value) throws IOException, JsonProcessingException {
        this.wrappedMapper.writeValue(stream, value);
    }
}
