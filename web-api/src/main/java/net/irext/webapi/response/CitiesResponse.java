package net.irext.webapi.response;

import net.irext.webapi.model.City;

import java.util.List;

/**
 * Filename:       CitiesResponse.java
 * Revised:        Date: 2017-04-07
 * Revision:       Revision: 1.0
 * <p>
 * Description:    List cities response
 * <p>
 * Revision log:
 * 2017-04-07: created by strawmanbobi
 */
public class CitiesResponse extends ServiceResponse {

    private List<City> entity;

    public CitiesResponse(Status status, List<City> cities) {
        super(status);
        this.entity = cities;
    }

    public CitiesResponse() {

    }

    public List<City> getEntity() {
        return entity;
    }

    public void setEntity(List<City> entity) {
        this.entity = entity;
    }
}
