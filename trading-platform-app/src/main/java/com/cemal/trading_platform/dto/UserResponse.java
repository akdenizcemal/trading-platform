package com.cemal.trading_platform.dto;

import java.math.BigDecimal;

public class UserResponse {
    private String username;
    private String email;
    private BigDecimal balance;
    private Long id;

    public UserResponse(Long id, String username, String email, BigDecimal balance) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.balance = balance;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }



    public BigDecimal getBalance() {
        return balance;
    }

    public String getUsername() {
        return username;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }


}
