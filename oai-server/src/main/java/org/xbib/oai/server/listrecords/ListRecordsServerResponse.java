package org.xbib.oai.server.listrecords;

import org.xbib.oai.server.ServerOAIResponse;

import java.util.Date;

/**
 *
 */
public class ListRecordsServerResponse extends ServerOAIResponse {

    private String error;

    private Date date;

    private long expire;

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public long getExpire() {
        return expire;
    }

}
