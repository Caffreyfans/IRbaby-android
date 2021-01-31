package top.caffreyfans.irbaby.model;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

public class ApplianceInfo extends LitePalSupport implements Serializable {

    private int id;
    private String file;
    private String name;
    private String mac;
    private String ip;
    private String signal;
    private int brand = -1;
    private int category = -1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public int getCategory() {
        return category;
    }

    public void setCategory(int cotegory) {
        this.category = cotegory;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) { this.file = file; }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getBrand() {
        return brand;
    }

    public void setBrand(int brand) {
        this.brand = brand;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSignal() { return signal; }

    public void setSignal(String signal) { this.signal = signal; }
}
