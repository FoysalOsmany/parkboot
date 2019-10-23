package com.park.parkboot.components.car;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface CarRepository extends PagingAndSortingRepository<Car, Integer> {
    public Car findByLicenseNumber(String licenseNumber);
}