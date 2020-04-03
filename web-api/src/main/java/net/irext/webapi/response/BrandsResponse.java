package net.irext.webapi.response;

import net.irext.webapi.model.Brand;

import java.util.List;

/**
 * Filename:       BrandsResponse.java
 * Revised:        Date: 2017-04-07
 * Revision:       Revision: 1.0
 * <p>
 * Description:    List brands response
 * <p>
 * Revision log:
 * 2017-04-07: created by strawmanbobi
 */
public class BrandsResponse extends ServiceResponse {

    private List<Brand> entity;

    public BrandsResponse(Status status, List<Brand> brands) {
        super(status);
        this.entity = brands;
    }

    public BrandsResponse() {

    }

    public List<Brand> getEntity() {
        return entity;
    }

    public void setEntity(List<Brand> entity) {
        this.entity = entity;
    }
}
