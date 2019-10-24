package com.park.parkboot.components.parkinglot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ParkingLotController {
    ParkingLotRepository parkingLotRepository;
    CarRepository carRepository;
    ParkingLotService parkingLotService;

    @Autowired
    public ParkingLotController(ParkingLotRepository parkingLotRepository, CarRepository carRepository,
            ParkingLotService parkingLotService) {
        this.parkingLotRepository = parkingLotRepository;
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
    public ResponseEntity<ObjectNode> unparkACarFromParkingLot(@PathVariable("licenseNumber") String licenseNumber) {
        Car car = carRepository.findByLicenseNumber(licenseNumber);

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

        return new ResponseEntity<ObjectNode>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/parkingLots/{parkingLotNumber}")
    public ResponseEntity<ObjectNode> parkingLotDetails(@PathVariable("parkingLotNumber") Integer parkingLotNumber) {
        Optional<ParkingLot> parkingLot = parkingLotRepository.findById(parkingLotNumber);

        if (!parkingLot.isPresent()) {
            return Util.errorResponse("No parking lot found for the parkingLotNumber", HttpStatus.NOT_FOUND);
        } else {
            Integer capacity = parkingLot.get().getCapacity();

            Integer occupied = carRepository.countByParkingLotParkingLotNumber(parkingLotNumber);

            ObjectNode response = JsonNodeFactory.instance.objectNode();

            response.put("name", parkingLot.get().getName());
            response.put("capacity", capacity);
            response.put("parkingLotNumber", parkingLotNumber);
            response.put("emptySlotCount", capacity - occupied);

            return new ResponseEntity<ObjectNode>(response, HttpStatus.OK);
        }
    }

    @GetMapping(path = "/parkingLots/{parkingLotNumber}/sameColor")
    public ResponseEntity<?> parkingLotCarsWithSameColor(@PathVariable("parkingLotNumber") Integer parkingLotNumber) {
        Optional<ParkingLot> parkingLot = parkingLotRepository.findById(parkingLotNumber);

        if (!parkingLot.isPresent()) {
            ObjectNode errorResponse = JsonNodeFactory.instance.objectNode();
            errorResponse.put("error", "No parking lot found for the parkingLotNumber");
            return new ResponseEntity<ObjectNode>(errorResponse, HttpStatus.NOT_FOUND);
        } else {
            List<Car> cars = parkingLot.get().getCars();

            HashMap<String, List<String>> carColorMap = new HashMap<String, List<String>>();

            cars.forEach(car -> {
                if (carColorMap.containsKey(car.getColor())) {
                    carColorMap.get(car.getColor()).add(car.getLicenseNumber());
                } else {
                    List<String> licenseList = new ArrayList<String>();
                    licenseList.add(car.getLicenseNumber());
                    carColorMap.put(car.getColor(), licenseList);
                }
            });

            return new ResponseEntity<HashMap<String, List<String>>>(carColorMap, HttpStatus.OK);
        }
    }
}
