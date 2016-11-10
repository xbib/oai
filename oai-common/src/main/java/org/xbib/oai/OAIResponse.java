package org.xbib.oai;

import java.io.IOException;
import java.io.Writer;

/**
 *  OAI response.
 */
public interface OAIResponse {

    void to(Writer writer) throws IOException;
}
