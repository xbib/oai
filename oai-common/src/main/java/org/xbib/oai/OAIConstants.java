package org.xbib.oai;

/**
 *
 */
public interface OAIConstants {

    String USER_AGENT = "OAI/20161111";

    String NS_URI = "http://www.openarchives.org/OAI/2.0/";
    
    String NS_PREFIX = "oai";

    String OAIDC_NS_URI = "http://www.openarchives.org/OAI/2.0/oai_dc/";
    
    String OAIDC_NS_PREFIX = "oai_dc";

    String DC_NS_URI = "http://www.purl.org/dc/elements/1.1/";

    String DC_PREFIX = "dc";
    
    String VERB_PARAMETER = "verb";

    String IDENTIFY = "Identify";
    
    String LIST_METADATA_FORMATS = "ListMetadataFormats";

    String LIST_SETS = "ListSets";
    
    String LIST_RECORDS = "ListRecords";

    String LIST_IDENTIFIERS = "ListIdentifiers";
    
    String GET_RECORD = "GetRecord";
    
    String FROM_PARAMETER = "from";
    
    String UNTIL_PARAMETER = "until";
    
    String SET_PARAMETER = "set";
    
    String METADATA_PREFIX_PARAMETER = "metadataPrefix";
    
    String RESUMPTION_TOKEN_PARAMETER = "resumptionToken";
    
    String IDENTIFIER_PARAMETER = "identifier";
    
    String REQUEST = "request";
}
