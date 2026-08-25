package com.cemal.trading_platform.dto;


import java.math.BigDecimal;

public class HoldingResponse {

    private String stockName;
    private String symbol;
    private int shares;
    private BigDecimal currentPrice;
    private BigDecimal currentValue;

    public HoldingResponse(String stockName, String symbol, int shares,
                           BigDecimal currentPrice, BigDecimal currentValue) {
        this.stockName = stockName;
        this.symbol = symbol;
        this.shares = shares;
        this.currentPrice = currentPrice;
        this.currentValue = currentValue;
    }

    public String getStockName() {
        return stockName;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getShares() {
        return shares;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }
}