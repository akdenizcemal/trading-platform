package com.cemal.trading_platform.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private  String password;
    private BigDecimal balance;

    public void setPassword(String password) {
        this.password = password;
    }


    public String getPassword() {
        return password;
    }




    public void setUsername(String username) {
        this.username = username;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public String getEmail() {
        return email;
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


}
