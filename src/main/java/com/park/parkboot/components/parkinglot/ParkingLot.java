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
}
