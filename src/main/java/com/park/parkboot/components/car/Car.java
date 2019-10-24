package com.park.parkboot.components.car;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;

import com.park.parkboot.components.parkinglot.ParkingLot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Car implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;

    @Column
    private String licenseNumber;

    @Column
    private String color;

    @Column
    private Date checkinTime;

    @Column
    private Integer slotNumber;

    @ManyToOne(targetEntity = ParkingLot.class)
    @JoinColumn(name = "parkingLotNumber")
    private ParkingLot parkingLot;

    @PrePersist
    void preInsert() {
        if (this.checkinTime == null)
            this.checkinTime = new Date();

        this.slotNumber = parkingLot.getCars().size() + 1;
    }
}
