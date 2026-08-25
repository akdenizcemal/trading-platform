package com.cemal.trading_platform.dto;

import com.cemal.trading_platform.model.TradeType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class TradeRequest {
    @NotNull
    private Long userId;
    @NotNull
    private Long stockId;
    @Min(value = 1, message = "Shares must be at least 1")
    private int shares;
    @NotNull
    private TradeType tradeType;
    public Long getUserId() {
        return userId;
    }

    public Long getStockId() {
        return stockId;
    }

    public int getShares() {
        return shares;
    }

    public TradeType getTradeType() {
        return tradeType;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public void setTradeType(TradeType tradeType) {
        this.tradeType = tradeType;
    }
}
