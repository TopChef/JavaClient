package ca.uwaterloo.iqc.topchef.exceptions;

/**
 * Thrown if an attempt was made to send a request to the server where an instance did not match the schema
 */
public class SchemaMismatchException extends HTTPException {
    public SchemaMismatchException(Throwable error){
        super(error);
    }
}
