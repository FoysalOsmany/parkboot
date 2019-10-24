package com.park.parkboot.utils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Util {
    public static long getTimeDiff(Date toDate, Date fromDate, TimeUnit timeUnit) {
        long diffInMillies = toDate.getTime() - fromDate.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static ResponseEntity errorResponse(String message, HttpStatus status) {
        ObjectNode errorResponse = JsonNodeFactory.instance.objectNode();

        errorResponse.put("error", message);

        return new ResponseEntity<ObjectNode>(errorResponse, status);
    }
}