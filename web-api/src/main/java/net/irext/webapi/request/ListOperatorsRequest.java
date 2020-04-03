package net.irext.webapi.request;

/**
 * Filename:       ListOperatorsRequest.java
 * Revised:        Date: 2017-04-10
 * Revision:       Revision: 1.0
 * <p>
 * Description:    HTTP list STB operators request
 * <p>
 * Revision log:
 * 2017-04-10: created by strawmanbobi
 */
public class ListOperatorsRequest extends BaseRequest {

    private int from;
    private int count;
    private String cityCode;

    public ListOperatorsRequest(int from, int count, String cityCode) {
        this.from = from;
        this.count = count;
        this.cityCode = cityCode;
    }

    public ListOperatorsRequest() {

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

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }
}
