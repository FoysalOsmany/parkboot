package com.park.parkboot.components.parking;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.park.parkboot.components.car.Car;
import com.park.parkboot.components.car.CarRepository;
import com.park.parkboot.components.parkinglot.ParkingLot;
import com.park.parkboot.components.parkinglot.ParkingLotService;
import com.park.parkboot.utils.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParkingService {
    CarRepository carRepository;
    ParkingLotService parkingLotService;

    @Autowired
    public ParkingService(CarRepository carRepository, ParkingLotService parkingLotService) {
        this.carRepository = carRepository;
        this.parkingLotService = parkingLotService;
    }

    public ObjectNode parkACar(String parkingRule, String licenseNumber, String color) {
        ParkingLot parkingLot = this.parkingLotService.getParkingLotByParkingRule(parkingRule);

        if (parkingLot.getCapacity() > parkingLot.getCars().size()) {
            Car car = new Car();

            car.setLicenseNumber((String) licenseNumber);
            car.setColor((String) color);
            car.setParkingLot(parkingLot);

            Car savedCar = carRepository.save(car);

            ObjectNode data = JsonNodeFactory.instance.objectNode();

            data.put("parkingId", savedCar.getId());
            data.put("parkingLotNumber", parkingLot.getParkingLotNumber());

            return data;
        } else {
            throw new IllegalArgumentException("No Parking Slot Available");
        }
    }

    public ObjectNode unparkACar(String licenseNumber) {
        Car car = carRepository.findByLicenseNumber(licenseNumber);

        if (car == null) {
            throw new IllegalArgumentException("No Car found in this licenseNumber");
        }

        String color = car.getColor();

        Date checkinTime = car.getCheckinTime();

        Double perHourRate = car.getParkingLot().getPerHourRate();

        Long durationOfParkingInMinutes = Util.getTimeDiff(new Date(), checkinTime, TimeUnit.MINUTES);

        Double billAmount = (durationOfParkingInMinutes.doubleValue() / 60) * perHourRate;

        ObjectNode data = JsonNodeFactory.instance.objectNode();

        ObjectNode carInfo = JsonNodeFactory.instance.objectNode();

        carInfo.put("color", color);
        carInfo.put("licenseNumber", licenseNumber);
        data.set("car", carInfo);
        data.put("perHourRate", perHourRate);
        data.put("billAmount",
                BigDecimal.valueOf(billAmount).setScale(2, RoundingMode.HALF_UP).toString().concat(" MYR"));
        data.put("durationOfParking", durationOfParkingInMinutes.toString().concat(" Minutes"));

        try {
            carRepository.deleteById(car.getId());

            return data;
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not unpark the Car");
        }
    }
}
