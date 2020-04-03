package net.irext.webapi.model;

/**
 * Filename:       Category.java
 * Revised:        Date: 2017-03-28
 * Revision:       Revision: 1.0
 * <p>
 * Description:    Category bean
 * <p>
 * Revision log:
 * 2017-03-28: created by strawmanbobi
 */
public class Category {

    private int id;
    private String name;
    private int status;
    private String updateTime;
    private String nameEn;
    private String nameTw;
    private String contributor;

    public Category(int id, String name, int status, String updateTime,
                    String nameEn, String nameTw, String contributor) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.updateTime = updateTime;
        this.nameEn = nameEn;
        this.nameTw = nameTw;
        this.contributor = contributor;
    }

    public Category() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameTw() {
        return nameTw;
    }

    public void setNameTw(String nameTw) {
        this.nameTw = nameTw;
    }

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }
}
