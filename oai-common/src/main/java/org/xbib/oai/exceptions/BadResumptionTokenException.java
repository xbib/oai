package org.xbib.oai.exceptions;

import org.xbib.oai.util.ResumptionToken;

/**
 *
 */
public class BadResumptionTokenException extends OAIException {

    private static final long serialVersionUID = 7384401627260164303L;

    public BadResumptionTokenException(ResumptionToken<?> token) {
        super(token != null ? token.toString() : null);
    }   
}
