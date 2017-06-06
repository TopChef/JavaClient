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
    private final com.fasterxml.jackson.databind.ObjectMapper wrappedMapper;

    public ObjectMapper(){
        this.wrappedMapper = new com.fasterxml.jackson.databind.ObjectMapper();
    }


    @Override
    public Object readValue(String JSON) throws IOException, JsonParseException, JsonMappingException {
        return this.wrappedMapper.readValue(JSON, Object.class);
    }

    @Override
    public <T> T readValue(String JSON, Class<T> typeOfJSON) throws IOException,
            JsonParseException, JsonMappingException {
        return this.wrappedMapper.readValue(JSON.getBytes(), typeOfJSON);
    }

    @Override
    public <T> T readValue(InputStream stream, Class<T> typeOfJSON) throws IOException, JsonParseException, JsonMappingException {
        return this.wrappedMapper.readValue(stream, typeOfJSON);
    }

    @Override
    public String writeValueAsString(Object value) throws JsonProcessingException {
        return this.wrappedMapper.writeValueAsString(value);
    }

    @Override
    public void writeValue(Writer writer, Object value) throws IOException, JsonProcessingException {
        this.wrappedMapper.writeValue(writer, value);
    }

    @Override
    public void writeValue(OutputStream stream, Object value) throws IOException, JsonProcessingException {
        this.wrappedMapper.writeValue(stream, value);
    }
}
