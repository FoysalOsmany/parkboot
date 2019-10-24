package com.park.parkboot.components.parkinglot;

import java.util.HashMap;
import java.util.List;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.park.parkboot.utils.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ParkingLotController {
    ParkingLotService parkingLotService;

    @Autowired
    public ParkingLotController(ParkingLotService parkingLotService) {
        this.parkingLotService = parkingLotService;
    }

    @GetMapping(path = "/parkingLots/{parkingLotNumber}")
    public ResponseEntity<?> parkingLotDetails(@PathVariable("parkingLotNumber") Integer parkingLotNumber) {
        if (parkingLotNumber == null) {
            return Util.errorResponse("Invalid Request Parameter", HttpStatus.BAD_REQUEST);
        }

        try {
            ObjectNode data = this.parkingLotService.getParkingLotDetails(parkingLotNumber);

            return new ResponseEntity<ObjectNode>(data, HttpStatus.OK);
        } catch (Exception e) {
            return Util.errorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/parkingLots/{parkingLotNumber}/sameColor")
    public ResponseEntity<?> parkingLotCarsWithSameColor(@PathVariable("parkingLotNumber") Integer parkingLotNumber) {
        if (parkingLotNumber == null) {
            return Util.errorResponse("Invalid Request Parameter", HttpStatus.BAD_REQUEST);
        }

        try {
            HashMap<String, List<String>> data = this.parkingLotService.getCarsWithSameColor(parkingLotNumber);

            return new ResponseEntity<HashMap<String, List<String>>>(data, HttpStatus.OK);
        } catch (Exception e) {
            return Util.errorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
