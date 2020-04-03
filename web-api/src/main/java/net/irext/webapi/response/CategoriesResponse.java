package net.irext.webapi.response;

import net.irext.webapi.model.Category;

import java.util.List;

/**
 * Filename:       CategoriesResponse.java
 * Revised:        Date: 2017-04-07
 * Revision:       Revision: 1.0
 * <p>
 * Description:    List categories response
 * <p>
 * Revision log:
 * 2017-04-07: created by strawmanbobi
 */
public class CategoriesResponse extends ServiceResponse {

    private List<Category> entity;

    public CategoriesResponse(Status status, List<Category> categories) {
        super(status);
        this.entity = categories;
    }

    public CategoriesResponse() {

    }

    public List<Category> getEntity() {
        return entity;
    }

    public void setEntity(List<Category> entity) {
        this.entity = entity;
    }
}
