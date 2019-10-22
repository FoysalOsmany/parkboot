package com.park.parkboot.components.parkinglot;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import com.park.parkboot.components.car.Car;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
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
}
