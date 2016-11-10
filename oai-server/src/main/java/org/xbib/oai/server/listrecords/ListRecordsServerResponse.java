package org.xbib.oai.server.listrecords;

import org.xbib.oai.server.ServerOAIResponse;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class ListRecordsServerResponse extends ServerOAIResponse {

    private static final Logger logger = Logger.getLogger(ListRecordsServerResponse.class.getName());

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

    @Override
    public void to(Writer writer) throws IOException {
        try {
            if (this.expire > 0L) {
                logger.log(Level.INFO, "waiting for {} seconds (retry-after)", expire);
                Thread.sleep(1000 * expire);
                this.expire = 0L;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.log(Level.WARNING, "interrupted");
        }
    }

}
