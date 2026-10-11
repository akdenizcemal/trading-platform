
package com.cemal.trading_platform.controller;

import com.cemal.trading_platform.service.StockNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class StockExceptionHandler {

    @ExceptionHandler(StockNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleStockNotFound(
            StockNotFoundException exception) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", exception.getMessage()));
    }
}
