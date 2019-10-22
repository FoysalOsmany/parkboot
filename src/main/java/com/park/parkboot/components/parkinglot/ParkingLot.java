package com.park.parkboot.components.parkinglot;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import com.park.parkboot.components.car.Car;


@Entity
public class ParkingLot implements Serializable {
    @Id
    @GeneratedValue
    private Integer parkingLotNumber;

    @Column
    private String name;

    @Column
    private Integer capacity;

    @OneToMany(cascade=CascadeType.ALL, targetEntity = Car.class)
    @JoinColumn(name = "id")
    private Set<Car> carsList = new HashSet<Car>();

    public ParkingLot() {

    }
    
    public ParkingLot(String name, Integer capacity) {
        super();
        this.name = name;
        this.capacity = capacity;
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

    public Set<Car> getCarList() {
        return carsList;
    }
}
