package com.park.parkboot.components.car;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Car implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;

    @Column
    private String licenseNumber;

    @Column
    private Integer color;

    public Car(String licenseNumber, Integer color) {
        this.licenseNumber = licenseNumber;
        this.color = color;
    }

    protected Car() {
    }

    public String getLicenseNumber() {
        return this.licenseNumber;
    }

    public Integer getColor() {
        return this.color;
    }
}
