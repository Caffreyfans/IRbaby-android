package net.irext.webapi.response;

import net.irext.webapi.model.RemoteIndex;

import java.util.List;

/**
 * Filename:       IndexesResponse.java
 * Revised:        Date: 2017-04-12
 * Revision:       Revision: 1.0
 * <p>
 * Description:    List remote indexes response
 * <p>
 * Revision log:
 * 2017-04-12: created by strawmanbobi
 */
public class IndexesResponse extends ServiceResponse {

    private List<RemoteIndex> entity;

    public IndexesResponse(Status status, List<RemoteIndex> indexes) {
        super(status);
        this.entity = indexes;
    }

    public IndexesResponse() {

    }

    public List<RemoteIndex> getEntity() {
        return entity;
    }

    public void setEntity(List<RemoteIndex> entity) {
        this.entity = entity;
    }
}
