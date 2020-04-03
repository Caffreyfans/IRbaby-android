package net.irext.webapi.request;

/**
 * Filename:       AppSignInRequest.java
 * Revised:        Date: 2017-05-27
 * Revision:       Revision: 1.0
 * <p>
 * Description:    HTTP admin login request
 * <p>
 * Revision log:
 * 2017-05-27: created by strawmanbobi
 */
public class AppSignInRequest extends BaseRequest {

    private String appKey;
    private String appSecret;
    private int appType;
    private String iOSID;
    private String androidPackageName;
    private String androidSignature;

    public AppSignInRequest(String appKey, String appSecret, int appType,
                            String iOSID, String androidPackageName, String androidSignature) {
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.appType = appType;
        this.iOSID = iOSID;
        this.androidPackageName = androidPackageName;
        this.androidSignature = androidSignature;
    }

    public AppSignInRequest() {

    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public int getAppType() {
        return appType;
    }

    public void setAppType(int appType) {
        this.appType = appType;
    }

    public String getiOSID() {
        return iOSID;
    }

    public void setiOSID(String iOSID) {
        this.iOSID = iOSID;
    }

    public String getAndroidPackageName() {
        return androidPackageName;
    }

    public void setAndroidPackageName(String androidPackageName) {
        this.androidPackageName = androidPackageName;
    }

    public String getAndroidSignature() {
        return androidSignature;
    }

    public void setAndroidSignature(String androidSignature) {
        this.androidSignature = androidSignature;
    }
}
