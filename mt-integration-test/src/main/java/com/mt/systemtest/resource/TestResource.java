package com.mt.systemtest.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestResource {

    public static final String DEFAULT_TEST_RESPONSE_BODY = "\"test\":\"test\"";

    @GetMapping("delay/{delay}")
    public ResponseEntity<?> delay(@PathVariable String delay) {
        try {
            Thread.sleep(Long.parseLong(delay));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("test");
    }

    @GetMapping("get/{responseBody}")
    public ResponseEntity<?> get(@PathVariable String responseBody) {
        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("cache")
    public ResponseEntity<?> get2() {
        return ResponseEntity.ok().body(DEFAULT_TEST_RESPONSE_BODY);
    }

    @GetMapping("status/{status}")
    public ResponseEntity<?> status(@PathVariable String status) {
        HttpStatus resolve = HttpStatus.resolve(Integer.parseInt(status));
        return ResponseEntity.status(resolve).body(DEFAULT_TEST_RESPONSE_BODY);
    }
}
