package net.irext.webapi.response;

/**
 * Filename:       DecodeResponse.java
 * Revised:        Date: 2017-05-16
 * Revision:       Revision: 1.0
 * <p>
 * Description:    Online decode response
 * <p>
 * Revision log:
 * 2017-05-16: created by strawmanbobi
 */
public class DecodeResponse extends ServiceResponse {

    private int[] entity;

    public DecodeResponse(Status status, int[] entity) {
        super(status);
        this.entity = entity;
    }

    public DecodeResponse() {

    }

    public int[] getEntity() {
        return entity;
    }

    public void setEntity(int[] entity) {
        this.entity = entity;
    }
}
