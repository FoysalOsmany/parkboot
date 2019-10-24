package com.park.parkboot.components.parking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.park.parkboot.components.parking.ParkingService;
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
public class ParkingController {
    ParkingService parkingService;

    @Autowired
    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @PostMapping(path = "/park", consumes = "application/json")
    public ResponseEntity<?> parkACarInParkingLot(@RequestBody JsonNode parkReqMap) {
        String parkingRule = parkReqMap.get("parkingRule").asText().toString();
        JsonNode carInfo = parkReqMap.get("car");
        String licenseNumber = carInfo.findValue("licenseNumber").asText();
        String color = carInfo.findValue("color").asText();

        if ((!parkingRule.matches("fillFirst") && !parkingRule.matches("evenDistribution")) 
            || licenseNumber == null
            || color == null) {
            return Util.errorResponse("Invalid Request Parameter", HttpStatus.BAD_REQUEST);
        }

        try {
            ObjectNode data = this.parkingService.parkACar(parkingRule, licenseNumber, color);

            return new ResponseEntity<ObjectNode>(data, HttpStatus.OK);
        } catch (Exception e) {
            return Util.errorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path = "/unpark/{licenseNumber}")
    public ResponseEntity<?> unparkACarFromParkingLot(@PathVariable("licenseNumber") String licenseNumber) {
        if (licenseNumber == null) {
            return Util.errorResponse("Invalid Request Parameter", HttpStatus.BAD_REQUEST);
        }

        try {
            ObjectNode data = this.parkingService.unparkACar(licenseNumber);

            return new ResponseEntity<ObjectNode>(data, HttpStatus.OK);
        } catch (Exception e) {
            return Util.errorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
