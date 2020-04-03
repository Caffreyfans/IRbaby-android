package net.irext.webapi.response;

import net.irext.webapi.model.StbOperator;

import java.util.List;

/**
 * Filename:       OperatorsResponse.java
 * Revised:        Date: 2017-04-10
 * Revision:       Revision: 1.0
 * <p>
 * Description:    List STB operators response
 * <p>
 * Revision log:
 * 2017-04-10: created by strawmanbobi
 */
public class OperatorsResponse extends ServiceResponse {

    private List<StbOperator> entity;

    public OperatorsResponse(Status status, List<StbOperator> cities) {
        super(status);
        this.entity = cities;
    }

    public OperatorsResponse() {

    }

    public List<StbOperator> getEntity() {
        return entity;
    }

    public void setEntity(List<StbOperator> entity) {
        this.entity = entity;
    }
}
