package com.park.parkboot.components.parkinglot;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
}
