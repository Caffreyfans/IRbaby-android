package net.irext.webapi.request;

import com.google.gson.Gson;

/**
 * Filename:       BaseRequest.java
 * Revised:        Date: 2017-04-07
 * Revision:       Revision: 1.0
 * <p>
 * Description:    authentication factors included
 * <p>
 * Revision log:
 * 2017-04-07: created by strawmanbobi
 */
public class BaseRequest {

    private int id;
    private String token;

    public BaseRequest(int id, String token) {
        this.id = id;
        this.token = token;
    }

    BaseRequest() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String toJson() {
        return new Gson().toJson(this, this.getClass());
    }
}
