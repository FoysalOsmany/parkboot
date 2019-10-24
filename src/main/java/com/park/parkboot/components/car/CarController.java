package com.park.parkboot.components.car;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.park.parkboot.components.car.Car;
import com.park.parkboot.components.car.CarRepository;
import com.park.parkboot.components.parkinglot.ParkingLot;
import com.park.parkboot.components.parkinglot.ParkingLotService;
import com.park.parkboot.utils.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CarController {
    CarRepository carRepository;
    ParkingLotService parkingLotService;

    @Autowired
    public CarController(CarRepository carRepository, ParkingLotService parkingLotService) {
        this.parkingLotService = parkingLotService;
        this.carRepository = carRepository;
    }

    @PostMapping(path = "/park", consumes = "application/json")
    public ResponseEntity<?> parkACarInParkingLot(@RequestBody JsonNode parkReqMap) {
        String parkingRule = parkReqMap.get("parkingRule").asText().toString();

        if (!parkingRule.matches("fillFirst") && !parkingRule.matches("evenDistribution")) {
            return Util.errorResponse("Invalid Parking Rule", HttpStatus.BAD_REQUEST);
        }

        JsonNode carInfo = parkReqMap.get("car");
        String licenseNumber = carInfo.findValue("licenseNumber").asText();
        String color = carInfo.findValue("color").asText();

        try {
            ParkingLot parkingLot = this.parkingLotService.getParkingLotByParkingRule(parkingRule);

            if (parkingLot.getCapacity() > parkingLot.getCars().size()) {
                Car car = new Car();

                car.setLicenseNumber((String) licenseNumber);
                car.setColor((String) color);
                car.setParkingLot(parkingLot);

                Car savedCar = carRepository.save(car);

                ObjectNode response = JsonNodeFactory.instance.objectNode();

                response.put("parkingId", savedCar.getId());
                response.put("parkingLotNumber", parkingLot.getParkingLotNumber());

                return new ResponseEntity<ObjectNode>(response, HttpStatus.OK);
            } else {
                return Util.errorResponse("No Parking Slot Available", HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (Exception e) {
            return Util.errorResponse("No Parking Lot Found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/unpark/{licenseNumber}")
    public ResponseEntity<?> unparkACarFromParkingLot(@PathVariable("licenseNumber") String licenseNumber) {
        Car car = carRepository.findByLicenseNumber(licenseNumber);

        if (car == null) {
            return Util.errorResponse("No Car found for license number", HttpStatus.NOT_FOUND);
        }

        String color = car.getColor();

        Date checkinTime = car.getCheckinTime();

        Double perHourRate = car.getParkingLot().getPerHourRate();

        Long durationOfParkingInMinutes = Util.getTimeDiff(new Date(), checkinTime, TimeUnit.MINUTES);

        Double billAmount = (durationOfParkingInMinutes.doubleValue() / 60) * perHourRate;

        ObjectNode response = JsonNodeFactory.instance.objectNode();

        ObjectNode carInfo = JsonNodeFactory.instance.objectNode();

        carInfo.put("color", color);
        carInfo.put("licenseNumber", licenseNumber);
        response.set("car", carInfo);
        response.put("perHourRate", perHourRate);
        response.put("billAmount",
                BigDecimal.valueOf(billAmount).setScale(2, RoundingMode.HALF_UP).toString().concat(" MYR"));
        response.put("durationOfParking", durationOfParkingInMinutes.toString().concat(" Minutes"));

        try {
            carRepository.deleteById(car.getId());

            return new ResponseEntity<ObjectNode>(response, HttpStatus.OK);
        } catch (Exception e) {
            return Util.errorResponse("Could not unpark the Car", HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
