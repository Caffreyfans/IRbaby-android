package net.irext.webapi.bean;

import net.irext.webapi.utils.Constants;

/**
 * Filename:       ACStatus.java
 * Revised:        Date: 2017-03-28
 * Revision:       Revision: 1.0
 * <p>
 * Description:    Status descriptor for air-conditioner
 * <p>
 * Revision log:
 * 2017-03-28: created by strawmanbobi
 */
public class ACStatus {

    public Constants.ACPower acPower;
    public Constants.ACTemperature acTemp;
    public Constants.ACMode acMode;
    public Constants.ACWindDirection acWindDir;
    public Constants.ACWindSpeed acWindSpeed;
    public Constants.ACSwing acSwing;
    public int acDisplay;
    public int acSleep;
    public int acTimer;
}
