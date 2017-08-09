package com.takeadip.takeadip.model;

import java.io.Serializable;

/**
 * Created by vik on 23/05/2017.
 */

public class Dip implements Serializable {

    private String dip_id;
    private String name;
    private String description;
    private String pic;
    private String latitude;
    private String longitude;
    private String type;
    private String province;
    private String address;
    private Double distance;

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }



    public Dip()
    {

    }


    public String getDip_id() {
        return dip_id;
    }

    public void setDip_id(String dip_id) {
        this.dip_id = dip_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
