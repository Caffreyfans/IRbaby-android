package net.irext.webapi.response;

/**
 * Filename:       ServiceResponse.java
 * Revised:        Date: 2017-03-31
 * Revision:       Revision: 1.0
 * <p>
 * Description:    HTTP Response base class
 * <p>
 * Revision log:
 * 2017-03-31: created by strawmanbobi
 */
public class ServiceResponse {

    private Status status;

    public ServiceResponse(Status status) {
        this.status = status;
    }

    public ServiceResponse() {

    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
