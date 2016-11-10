package org.xbib.oai.exceptions;

/**
 *
 */
public class BadArgumentException extends OAIException {

    private static final long serialVersionUID = -6647892792394074500L;

    public BadArgumentException() {
        this(null);
    }

    public BadArgumentException(String message) {
        super(message);
    }   
}
