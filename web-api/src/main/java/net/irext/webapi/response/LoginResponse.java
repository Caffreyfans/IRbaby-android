package net.irext.webapi.response;

import net.irext.webapi.model.UserApp;
/**
 * Filename:       LoginResponse.java
 * Revised:        Date: 2017-03-31
 * Revision:       Revision: 1.0
 * <p>
 * Description:    HTTP Admin login response
 * <p>
 * Revision log:
 * 2017-03-31: created by strawmanbobi
 */
public class LoginResponse extends ServiceResponse {

    private UserApp entity;

    public LoginResponse(Status status, UserApp userApp) {
        super(status);
        this.entity = userApp;
    }

    public LoginResponse() {

    }

    public UserApp getEntity() {
        return entity;
    }

    public void setEntity(UserApp entity) {
        this.entity = entity;
    }
}
