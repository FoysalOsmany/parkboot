package com.park.parkboot.components.parking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.park.parkboot.components.car.CarRepository;
import com.park.parkboot.components.parkinglot.ParkingLot;
import com.park.parkboot.components.parkinglot.ParkingLotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class ParkingServiceTests {
    @MockBean
    private CarRepository carRepository;

    @MockBean
    private ParkingLotRepository parkingLotRepository;

    private ObjectNode data = JsonNodeFactory.instance.objectNode();

    @MockBean
    private ParkingService parkingService;

    @BeforeEach
    private void init() {
        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setName("BASEMENT");
        parkingLot.setCapacity(10);
        parkingLotRepository.save(parkingLot);

        data.put("parkingId", 1);
        data.put("parkingLotNumber", 1);
        when(parkingService.parkACar(null, null, null)).thenThrow(NullPointerException.class);
        when(parkingService.parkACar("fillFirst", "LI234", "color")).thenReturn(data);
    }

    @Test
    public void parkACar_throws_exception_for_null_data() throws Exception {
        assertThrows(NullPointerException.class, () -> {
            parkingService.parkACar(null, null, null);
        });
    }

    @Test
    public void parkACar_for_valid_data() {
        assertEquals(data, parkingService.parkACar("fillFirst", "LI234", "color"));
    }

}
