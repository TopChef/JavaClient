package ca.uwaterloo.iqc.topchef.adapters.com.fasterxml.jackson.core;

/**
 * Describes a type that requires an {@link ObjectMapper} to parse some JSON
 */
public interface RequiresJSONMapper {
    /**
     *
     * @return A JSON mapper
     */
    ObjectMapper getMapper();

    /**
     *
     * @param mapper The mapper to set
     */
    void setMapper(ObjectMapper mapper);
}
