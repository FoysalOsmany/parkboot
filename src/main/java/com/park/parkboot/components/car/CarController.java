package com.park.parkboot.components.car;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.park.parkboot.components.car.Car;
import com.park.parkboot.components.car.CarRepository;
import com.park.parkboot.utils.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CarController {
    CarRepository carRepository;

    @Autowired
    public CarController(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @GetMapping(path = "/find/{carLicenseNumber}")
    public ResponseEntity<?> findByLicenseNumber(@PathVariable("carLicenseNumber") String carLicenseNumber) {
        Car car = carRepository.findByLicenseNumber(carLicenseNumber);

        if (car == null) {
            return Util.errorResponse("No car found for the carLicenseNumber", HttpStatus.NOT_FOUND);
        } else {
            ObjectNode response = JsonNodeFactory.instance.objectNode();

            response.put("parkingLotNumber", car.getParkingLot().getParkingLotNumber());
            response.put("slotNumber", car.getSlotNumber());
            response.put("parkingId", car.getId());

            return new ResponseEntity<ObjectNode>(response, HttpStatus.OK);
        }
    }
}
