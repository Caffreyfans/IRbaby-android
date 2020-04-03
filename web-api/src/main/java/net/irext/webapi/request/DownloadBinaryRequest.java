package net.irext.webapi.request;

/**
 * Filename:       DownloadBinaryRequest.java
 * Revised:        Date: 2017-04-14
 * Revision:       Revision: 1.0
 * <p>
 * Description:    HTTP download IR binary
 * <p>
 * Revision log:
 * 2017-04-14: created by strawmanbobi
 */
public class DownloadBinaryRequest extends BaseRequest {

    private int indexId;

    public DownloadBinaryRequest(int indexId) {
        this.indexId = indexId;
    }

    public DownloadBinaryRequest() {

    }

    public int getIndexId() {
        return indexId;
    }

    public void setIndexId(int indexId) {
        this.indexId = indexId;
    }
}
