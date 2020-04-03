package net.irext.webapi.request;

/**
 * Filename:       ListCategoriesRequest.java
 * Revised:        Date: 2017-04-07
 * Revision:       Revision: 1.0
 * <p>
 * Description:    HTTP list categories request
 * <p>
 * Revision log:
 * 2017-04-07: created by strawmanbobi
 */
public class ListCategoriesRequest extends BaseRequest {

    private int from;
    private int count;

    public ListCategoriesRequest(int from, int count) {
        this.from = from;
        this.count = count;
    }

    public ListCategoriesRequest() {

    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
