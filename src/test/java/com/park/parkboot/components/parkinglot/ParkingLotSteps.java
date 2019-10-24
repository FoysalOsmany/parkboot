package com.park.parkboot.components.parkinglot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java8.En;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.park.parkboot.utils.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
/**
 * Step Definition Class for Employee.
 *
 * <p>
 * Uses Java Lambda style step definitions so that we don't need to worry about
 * method naming for each step definition
 * </p>
 */
public class ParkingLotSteps implements En {
    @Autowired
    private RestTemplate restTemplate;

    List<Map<String, String>> parkingLotInputs = new ArrayList<Map<String, String>>();

    

    public ParkingLotSteps() {
        Given("user wants to create a parking lot with the following attributes", (DataTable dt) -> {
            for (Map<String, String> row : dt.asMaps()) {
                String name = row.get("name");
                String capacity = row.get("capacity");

                Map<String, String> data = new HashMap<>();
                data.put("name", name);
                data.put("capacity", capacity);
                parkingLotInputs.add(data);
            }
        });

        When("user try to save the new parking lot", () -> {
            String createParkingLotEndpoint = Util.BASEURL.concat("/parkingLots");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            parkingLotInputs.forEach(p -> {
                Map<String, Object> map = new HashMap<>();
                map.put("name", p.get("name"));
                map.put("capacity", Integer.valueOf(p.get("capacity")));
                
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
                System.out.println(p.get("name"));
                System.out.println(p.get("capacity"));

                ResponseEntity<JsonNode> response = this.restTemplate.postForObject(createParkingLotEndpoint, entity, ResponseEntity.class);
                assertThat(response.getStatusCode()).isIn(200, 201);
            });
        });

        /**
         * This can be moved to a Class named 'CommonSteps.java so that it can be
         * reused.
         */
        Then("the save is {string}", (String expectedResult) -> {
            // switch (expectedResult) {
            // case "IS SUCCESSFUL":
            // assertThat(response.getStatusCode()).isIn(200, 201);
            // break;
            // case "FAILS":
            // assertThat(response.getStatusCode()).isIn(400, 500);
            // break;
            // default:
            // fail("Unexpected error");
            // }
        });

    }
}