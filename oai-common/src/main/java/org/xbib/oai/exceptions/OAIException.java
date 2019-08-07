package org.xbib.oai.exceptions;

/**
 *
 */
public class OAIException extends RuntimeException {

    private static final long serialVersionUID = -1890146067179892744L;

    public OAIException(String message) {
        super(message);
    }

    public OAIException(Throwable throwable) {
        super(throwable);
    }
    
    public OAIException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
