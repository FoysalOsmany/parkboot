package com.park.parkboot.components.parkinglot;

import java.util.List;

import com.park.parkboot.components.car.Car;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface ParkingLotRepository extends PagingAndSortingRepository<ParkingLot, Integer> {

}