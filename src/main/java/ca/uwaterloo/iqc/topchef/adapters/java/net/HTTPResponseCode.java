package ca.uwaterloo.iqc.topchef.adapters.java.net;

/**
 * Describes the possible HTTP status codes that can be returned by
 * making an HTTP request. These codes map onto familiar HTTP status
 * codes in the following way
 *
 * Enum                       | Status Code
 * -------------------------- | -----------
 *  ``OK``                    |   200
 *  ``CREATED``               |   201
 *  ``ACCEPTED``              |   202
 *  ``NO_CONTENT``            |   204
 *  ``BAD_REQUEST``           |   400
 *  ``NOT_FOUND``             |   404
 *  ``METHOD_NOT_ALLOWED``    |   405
 *  ``INTERNAL_SERVER_ERROR`` |   500
 *
 */
public enum HTTPResponseCode {
    OK, CREATED, ACCEPTED, NO_CONTENT, BAD_REQUEST, NOT_FOUND,
    METHOD_NOT_ALLOWED, INTERNAL_SERVER_ERROR
}
