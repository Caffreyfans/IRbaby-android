package net.irext.webapi.model;

/**
 * Filename:       RemoteIndex.java
 * Revised:        Date: 2017-03-28
 * Revision:       Revision: 1.0
 * <p>
 * Description:    RemoteIndex bean
 * <p>
 * Revision log:
 * 2017-03-28: created by strawmanbobi
 */
public class RemoteIndex {

    private int id;
    private int categoryId;
    private String categoryName;
    private int brandId;
    private String brandName;
    private String cityCode;
    private String cityName;
    private String operatorId;
    private String operatorName;
    private String protocol;
    private String remote;
    private String remoteMap;
    private int status;
    private int subCate;
    private int priority;
    private String remoteNumber;
    private String categoryNameTw;
    private String brandNameTw;
    private String cityNameTw;
    private String operatorNameTw;
    private String binaryMd5;
    private String contributor;
    private String updateTime;

    public RemoteIndex(int id,
                       int categoryId, String categoryName, int brandId, String brandName,
                       String cityCode, String cityName, String operatorId, String operatorName,
                       String protocol, String remote, String remoteMap, int status, int subCate,
                       int priority, String remoteNumber,
                       String categoryNameTw, String brandNameTw,
                       String cityNameTw, String operatorNameTw,
                       String binaryMd5, String contributor, String updateTime) {
        this.id = id;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.brandId = brandId;
        this.brandName = brandName;
        this.cityCode = cityCode;
        this.cityName = cityName;
        this.operatorId = operatorId;
        this.operatorName = operatorName;
        this.protocol = protocol;
        this.remote = remote;
        this.remoteMap = remoteMap;
        this.status = status;
        this.subCate = subCate;
        this.priority = priority;
        this.remoteNumber = remoteNumber;
        this.categoryNameTw = categoryNameTw;
        this.brandNameTw = brandNameTw;
        this.cityNameTw = cityNameTw;
        this.operatorNameTw = operatorNameTw;
        this.binaryMd5 = binaryMd5;
        this.contributor = contributor;
        this.updateTime = updateTime;
    }

    public RemoteIndex() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
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

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getRemote() {
        return remote;
    }

    public void setRemote(String remote) {
        this.remote = remote;
    }

    public String getRemoteMap() {
        return remoteMap;
    }

    public void setRemoteMap(String remoteMap) {
        this.remoteMap = remoteMap;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSubCate() {
        return subCate;
    }

    public void setSubCate(int subCate) {
        this.subCate = subCate;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getRemoteNumber() {
        return remoteNumber;
    }

    public void setRemoteNumber(String remoteNumber) {
        this.remoteNumber = remoteNumber;
    }

    public String getCategoryNameTw() {
        return categoryNameTw;
    }

    public void setCategoryNameTw(String categoryNameTw) {
        this.categoryNameTw = categoryNameTw;
    }

    public String getBrandNameTw() {
        return brandNameTw;
    }

    public void setBrandNameTw(String brandNameTw) {
        this.brandNameTw = brandNameTw;
    }

    public String getCityNameTw() {
        return cityNameTw;
    }

    public void setCityNameTw(String cityNameTw) {
        this.cityNameTw = cityNameTw;
    }

    public String getOperatorNameTw() {
        return operatorNameTw;
    }

    public void setOperatorNameTw(String operatorNameTw) {
        this.operatorNameTw = operatorNameTw;
    }

    public String getBinaryMd5() {
        return binaryMd5;
    }

    public void setBinaryMd5(String binaryMd5) {
        this.binaryMd5 = binaryMd5;
    }

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
