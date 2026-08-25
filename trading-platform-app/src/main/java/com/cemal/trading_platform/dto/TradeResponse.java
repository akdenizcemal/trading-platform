package com.cemal.trading_platform.dto;

import com.cemal.trading_platform.model.TradeType;

import java.math.BigDecimal;

public class TradeResponse {

    private String username;
    private String stockName;
    private String symbol;
    private int shares;
    private BigDecimal price;
    private TradeType tradeType;

    public TradeResponse(String username, String stockName, String symbol,
                         int shares, BigDecimal price, TradeType tradeType) {
        this.username = username;
        this.stockName = stockName;
        this.symbol = symbol;
        this.shares = shares;
        this.price = price;
        this.tradeType = tradeType;
    }

    public String getUsername() {
        return username;
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

    public BigDecimal getPrice() {
        return price;
    }

    public TradeType getTradeType() {
        return tradeType;
    }
}