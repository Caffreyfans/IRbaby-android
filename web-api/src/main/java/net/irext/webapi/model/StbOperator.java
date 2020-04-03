package net.irext.webapi.model;

/**
 * Filename:       StbOperator.java
 * Revised:        Date: 2017-03-28
 * Revision:       Revision: 1.0
 * <p>
 * Description:    StbOperator bean
 * <p>
 * Revision log:
 * 2017-03-28: created by strawmanbobi
 */
public class StbOperator {

    private int id;
    private String operatorId;
    private String operatorName;
    private String cityCode;
    private String cityName;
    private int status;
    private String operatorNameTw;

    public StbOperator(int id, String operatorId, String operatorName,
                       String cityCode, String cityName, int status, String operatorNameTw) {
        this.id = id;
        this.operatorId = operatorId;
        this.operatorName = operatorName;
        this.cityCode = cityCode;
        this.cityName = cityName;
        this.status = status;
        this.operatorNameTw = operatorNameTw;
    }

    public StbOperator() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOperatorNameTw() {
        return operatorNameTw;
    }

    public void setOperatorNameTw(String operatorNameTw) {
        this.operatorNameTw = operatorNameTw;
    }
}
