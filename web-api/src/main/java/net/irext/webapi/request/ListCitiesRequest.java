package net.irext.webapi.request;

/**
 * Filename:       ListCitiesRequest.java
 * Revised:        Date: 2017-04-07
 * Revision:       Revision: 1.0
 * <p>
 * Description:    HTTP list cities request
 * <p>
 * Revision log:
 * 2017-04-07: created by strawmanbobi
 */
public class ListCitiesRequest extends BaseRequest {

    private String provincePrefix;

    public ListCitiesRequest(String provincePrefix) {
        this.provincePrefix = provincePrefix;
    }

    public ListCitiesRequest() {

    }

    public String getProvincePrefix() {
        return provincePrefix;
    }

    public void setProvincePrefix(String provincePrefix) {
        this.provincePrefix = provincePrefix;
    }
}
