package com.cemal.trading_platform.service;

public class StockNotFoundException extends RuntimeException {
    public StockNotFoundException(long id) {
        super("Could not find stock with id " + id);

    }

}
