package com.park.parkboot.components.parkinglot;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class ParkingLotService {
    ParkingLotRepository parkingLotRepository;

    @Autowired
    public ParkingLotService(ParkingLotRepository parkingLotRepository) {
        this.parkingLotRepository = parkingLotRepository;
    }

    public ParkingLot addParkingLot(@Valid @RequestBody ParkingLot parkingLotModel) {
        return this.parkingLotRepository.save(parkingLotModel);
    }
} 


// @RestController
//  @RequestMapping("/api/parkingLots")
// public class ParkingLotController {
//     @Autowired
//     private ParkingLotRepository parkingLotRepository;

//     @GetMapping("/")
//     public List<ParkingLotModel> getAllParkingLot() {
//         return parkingLotRepository.findAll();
//     }
    
//     @PostMapping("/")
//     public ParkingLotModel enlistParkingLot(@Valid @RequestBody ParkingLotModel parkingLotModel) {
//         return parkingLotRepository.save(parkingLotModel);
//     }
// }