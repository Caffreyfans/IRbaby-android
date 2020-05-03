package top.caffreyfans.irbaby.model;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

public class DeviceInfo extends LitePalSupport implements Serializable {

    private int id;
    private String version;
    private String ip;
    private String mac;
    private String mqttAddress;
    private int mqttPort;
    private String mqttUser;
    private String mqttPassword;
    private int irSendPin;
    private int irReceivePin;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getMqttAddress() {
        return mqttAddress;
    }

    public void setMqttAddress(String mqttAddress) {
        this.mqttAddress = mqttAddress;
    }

    public int getMqttPort() {
        return mqttPort;
    }

    public void setMqttPort(int mqttPort) {
        this.mqttPort = mqttPort;
    }

    public String getMqttUser() {
        return mqttUser;
    }

    public void setMqttUser(String mqttUser) {
        this.mqttUser = mqttUser;
    }

    public String getMqttPassword() {
        return mqttPassword;
    }

    public void setMqttPassword(String mqttPassword) {
        this.mqttPassword = mqttPassword;
    }

    public int getIrSendPin() {
        return irSendPin;
    }

    public void setIrSendPin(int irSendPin) {
        this.irSendPin = irSendPin;
    }

    public int getIrReceivePin() {
        return irReceivePin;
    }

    public void setIrReceivePin(int irReceivePin) {
        this.irReceivePin = irReceivePin;
    }

}
