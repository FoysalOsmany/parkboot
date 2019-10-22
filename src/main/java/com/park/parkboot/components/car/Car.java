package com.park.parkboot.components.car;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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
}
