package org.xbib.oai.exceptions;

@SuppressWarnings("serial")
public class TooManyRequestsException extends OAIException {
    public TooManyRequestsException(String message) {
        super(message);
    }
}
