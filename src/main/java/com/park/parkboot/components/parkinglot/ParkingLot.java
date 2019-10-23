package com.park.parkboot.components.parkinglot;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;

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

    @Column
    private Double perHourRate;

    @OneToMany(mappedBy="parkingLot", targetEntity = Car.class)
    private List<Car> cars;

    @PrePersist
    void preInsert() {
    if (this.perHourRate == null)
        this.perHourRate = 1.0;
    }
}
