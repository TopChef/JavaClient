package ca.uwaterloo.iqc.topchef.endpoints.abstract_endpoints;

import ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core.RequiresJSONMapper;
import ca.uwaterloo.iqc.topchef.exceptions.HTTPException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.List;

/**
 * Describes an endpoint capable of getting JSON via an HTTP GET request
 */
public interface ImmutableJSONEndpoint extends Endpoint, RequiresJSONMapper {

    /**
     *
     * @return An object built from JSON retrieved from this API endpoint
     * @throws JsonParseException If the JSON cannot be parsed
     * @throws IOException If underlying I/O objects throw exceptions
     * @throws HTTPException If I/O between the application and server is OK, but the server
     * does something unexpected
     */
    Object getJSON() throws JsonParseException, IOException, HTTPException;

    /**
     *
     * @param desiredType The class to which the JSON from the server is to be marshalled
     * @param <T> The type to which the data is to be marshalled
     * @return An instance of the desired type
     */
    <T> T getJSON(Class<T> desiredType) throws JsonMappingException,
            JsonParseException, IOException, HTTPException;

    /**
     *
     * @param <D> The type to which the "data" field is to be marshalled
     * @param <M> The type to which the "meta" field is to be marshalled
     * @param <L> The type of links
     */
    class DataResponse<D, M, L> {
        @Getter
        @Setter
        private D data;

        @Getter
        @Setter
        private M meta;

        @Getter
        @Setter
        private L links;
    }

    /**
     *
     * @param <E> The type to which elements in the "errors" field are to be marshalled
     * @param <M> The type to which the "meta" field is to be marshalled
     * @param <L> The links type
     */
    class ErrorResponse<E, M, L> {
        @Getter
        @Setter
        private List<E> errors;

        @Getter
        @Setter
        private M meta;

        @Getter
        @Setter
        private L links;
    }
}
