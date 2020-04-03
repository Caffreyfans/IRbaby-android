package net.irext.webapi.request;

/**
 * Filename:       ListBrandsRequest.java
 * Revised:        Date: 2017-04-07
 * Revision:       Revision: 1.0
 * <p>
 * Description:    HTTP list brands request
 * <p>
 * Revision log:
 * 2017-04-07: created by strawmanbobi
 */
public class ListBrandsRequest extends BaseRequest {

    private int categoryId;
    private int from;
    private int count;

    public ListBrandsRequest(int categoryId, int from, int count) {
        this.categoryId = categoryId;
        this.from = from;
        this.count = count;
    }

    public ListBrandsRequest() {

    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
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
