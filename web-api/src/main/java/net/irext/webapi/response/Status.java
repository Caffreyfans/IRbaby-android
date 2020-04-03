package net.irext.webapi.response;

/**
 * Filename:       Status.java
 * Revised:        Date: 2017-03-31
 * Revision:       Revision: 1.0
 * <p>
 * Description:    HTTP response status
 * <p>
 * Revision log:
 * 2017-03-31: created by strawmanbobi
 */
public class Status {

    private int code;
    private String cause;

    public Status(int code, String cause) {
        this.code = code;
        this.cause = cause;
    }

    public Status() {

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }
}
