package com.park.parkboot.components.parkinglot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.park.parkboot.components.car.Car;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParkingLotService {
    ParkingLotRepository parkingLotRepository;

    @Autowired
    public ParkingLotService(ParkingLotRepository parkingLotRepository) {
        this.parkingLotRepository = parkingLotRepository;
    }

    public ParkingLot getParkingLotByParkingRule(String parkingRule) {
        switch (parkingRule) {
        case "fillFirst":
            Iterable<ParkingLot> parkingLots = this.parkingLotRepository.findAll();

            Collections.sort((List<ParkingLot>) parkingLots,
                    (Comparator<? super ParkingLot>) (ParkingLot a, ParkingLot b) -> {
                        Integer aHasEmptySlot = (a.getCapacity() - a.getCars().size()) > 0 ? 1
                                : Integer.MAX_VALUE / a.getParkingLotNumber();
                        Integer bHasEmptySlot = (b.getCapacity() - b.getCars().size()) > 0 ? 1
                                : Integer.MAX_VALUE / b.getParkingLotNumber();
                        Integer aSortPriority = a.getParkingLotNumber() * aHasEmptySlot;
                        Integer bSortPriority = b.getParkingLotNumber() * bHasEmptySlot;

                        return aSortPriority.compareTo(bSortPriority);
                    });

            if (parkingLots.iterator().hasNext()) {
                return parkingLots.iterator().next();
            } else {
                throw new IllegalArgumentException("No Parking Lot Added");
            }
        case "evenDistribution":
            Iterable<ParkingLot> allParkingLots = this.parkingLotRepository.findAll();

            Collections.sort((List<ParkingLot>) allParkingLots,
                    (Comparator<? super ParkingLot>) (ParkingLot a, ParkingLot b) -> {
                        Integer aEmptiness = a.getCapacity() - a.getCars().size();
                        Integer bEmptiness = b.getCapacity() - b.getCars().size();

                        return bEmptiness.compareTo(aEmptiness);
                    });

            if (allParkingLots.iterator().hasNext()) {
                return allParkingLots.iterator().next();
            } else {
                throw new IllegalArgumentException("No Parking Lot Added");
            }
        default:
            throw new IllegalArgumentException("INVALID parkingRule");
        }
    }

    public HashMap<String, List<String>> getCarsWithSameColor(Integer parkingLotNumber) {
        Optional<ParkingLot> parkingLot = parkingLotRepository.findById(parkingLotNumber);

        if (!parkingLot.isPresent()) {
            throw new IllegalArgumentException("No parking lot found for the parkingLotNumber");
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

            return carColorMap;
        }
    }

    public ObjectNode getParkingLotDetails(Integer parkingLotNumber) {
        Optional<ParkingLot> parkingLot = parkingLotRepository.findById(parkingLotNumber);

        if (!parkingLot.isPresent()) {
            throw new IllegalArgumentException("No parking lot found for the parkingLotNumber");
        } else {
            Integer capacity = parkingLot.get().getCapacity();

            Integer occupied = capacity - parkingLot.get().getCars().size();

            ObjectNode data = JsonNodeFactory.instance.objectNode();

            data.put("name", parkingLot.get().getName());
            data.put("capacity", capacity);
            data.put("parkingLotNumber", parkingLotNumber);
            data.put("emptySlotCount", capacity - occupied);

            return data;
        }
    }
}
