package com.park.parkboot.components.parkinglot;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ParkingLot implements Serializable {
    @Id
    @GeneratedValue
    private Integer parkingLotNumber;

    @Column
    private String name;

    @Column
    private Integer capacity;

    public ParkingLot(String name, Integer capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    protected ParkingLot() {
    }

    public Integer getParkingLotNumber() {
        return this.parkingLotNumber;
    }

    public String getName() {
        return this.name;
    }

    public Integer getCapacity() {
        return this.capacity;
    }
}
