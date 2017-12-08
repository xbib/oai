package org.xbib.oai.client.listrecords;

import org.xbib.oai.OAIConstants;
import org.xbib.oai.client.AbstractOAIRequest;
import org.xbib.oai.xml.MetadataHandler;

import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class ListRecordsRequest extends AbstractOAIRequest {

    private List<MetadataHandler> handlers = new LinkedList<>();

    public ListRecordsRequest() {
        super();
        addParameter(OAIConstants.VERB_PARAMETER, LIST_RECORDS);
    }
    public ListRecordsRequest addHandler(MetadataHandler handler) {
        handlers.add(handler);
        return this;
    }

    public List<MetadataHandler> getHandlers() {
        return handlers;
    }

}
