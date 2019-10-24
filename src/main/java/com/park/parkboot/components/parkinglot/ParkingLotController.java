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
