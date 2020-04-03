package net.irext.webapi.bean;

/**
 * Filename:       TemperatureRange.java
 * Revised:        Date: 2017-03-28
 * Revision:       Revision: 1.0
 * <p>
 * Description:    Temperature range for air-conditioner
 * <p>
 * Revision log:
 * 2017-03-28: created by strawmanbobi
 */
public class TemperatureRange {

    private static final String TAG = TemperatureRange.class.getSimpleName();

    private int tempMin;
    private int tempMax;

    public TemperatureRange() {
    }

    public TemperatureRange(int tempMin, int tempMax) {
        this.tempMin = tempMin;
        this.tempMax = tempMax;
    }

    public int getTempMin() {
        return tempMin;
    }

    public void setTempMin(int tempMin) {
        this.tempMin = tempMin;
    }

    public int getTempMax() {
        return tempMax;
    }

    public void setTempMax(int tempMax) {
        this.tempMax = tempMax;
    }
}
