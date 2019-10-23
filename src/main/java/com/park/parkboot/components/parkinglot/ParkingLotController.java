package com.park.parkboot.components.parkinglot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ParkingLotController {
    ParkingLotRepository parkingLotRepository;
    CarRepository carRepository;

    @Autowired
    public ParkingLotController(ParkingLotRepository parkingLotRepository, CarRepository carRepository) {
        this.parkingLotRepository = parkingLotRepository;
        this.carRepository = carRepository;
    }

    @PostMapping(path = "/park", consumes = "application/json")
    public ResponseEntity<ObjectNode> parkACarInParkingLot(@RequestBody  JsonNode parkReqMap) {
        Iterable<ParkingLot> parkingLots = this.parkingLotRepository.findAll();

        String parkingRule = parkReqMap.get("parkingRule").asText();
        JsonNode carInfo = parkReqMap.get("car");
        String licenseNumber = carInfo.findValue("licenseNumber").asText();
        String color = carInfo.findValue("color").asText();
        
        ParkingLot parkingLot = parkingLots.iterator().next();

        Car car = new Car();

        car.setLicenseNumber((String) licenseNumber);
        car.setColor((String) color);
        car.setParkingLot(parkingLot);

        Car savedCar = carRepository.save(car);

        ObjectNode response = JsonNodeFactory.instance.objectNode();

        response.put("parkingId", savedCar.getId());
        response.put("parkingLotNumber", parkingLot.getParkingLotNumber());

        return new ResponseEntity<ObjectNode>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/unpark/{licenseNumber}")
    public ResponseEntity<ObjectNode> unparkACarFromParkingLot(@PathVariable("licenseNumber") String licenseNumber) {
        Car car = carRepository.findByLicenseNumber(licenseNumber);

        String color = car.getColor();

        Date checkinTime = car.getCheckinTime();

        Double perHourRate = car.getParkingLot().getPerHourRate(); 

        Long durationOfParkingInMinutes = Util.getTimeDiff(new Date(), checkinTime, TimeUnit.MINUTES);

        Double billAmount = (durationOfParkingInMinutes.doubleValue() / 60) * perHourRate;

        ObjectNode response = JsonNodeFactory.instance.objectNode();

        response.put("color", color);
        response.put("licenseNumber", licenseNumber);
        response.put("perHourRate", perHourRate);
        response.put("billAmount", BigDecimal.valueOf(billAmount).setScale(2, RoundingMode.HALF_UP).toString().concat(" MYR"));
        response.put("durationOfParking", durationOfParkingInMinutes.toString().concat(" Minutes"));

        return new ResponseEntity<ObjectNode>(response, HttpStatus.OK);
    }
}
