package net.irext.webapi.request;

import net.irext.webapi.bean.ACStatus;

/**
 * Filename:       DecodeRequest.java
 * Revised:        Date: 2017-05-16
 * Revision:       Revision: 1.0
 * <p>
 * Description:    HTTP decode online
 * <p>
 * Revision log:
 * 2017-05-16: created by strawmanbobi
 */
public class DecodeRequest extends BaseRequest {

    private int indexId;
    private ACStatus acStatus;
    private int keyCode;
    private int changeWindDir;

    public DecodeRequest(int indexId, ACStatus acStatus, int keyCode, int changeWindDir) {
        this.indexId = indexId;
        this.acStatus = acStatus;
        this.keyCode = keyCode;
        this.changeWindDir = changeWindDir;
    }

    public DecodeRequest() {

    }

    public int getIndexId() {
        return indexId;
    }

    public void setIndexId(int indexId) {
        this.indexId = indexId;
    }

    public ACStatus getAcStatus() {
        return acStatus;
    }

    public void setAcStatus(ACStatus acStatus) {
        this.acStatus = acStatus;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getChangeWindDir() {
        return changeWindDir;
    }

    public void setChangeWindDir(int changeWindDir) {
        this.changeWindDir = changeWindDir;
    }
}
