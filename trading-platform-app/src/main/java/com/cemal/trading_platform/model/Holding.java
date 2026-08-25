package com.cemal.trading_platform.model;

import jakarta.persistence.*;

@Entity
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public void setShares(int shares) {
        this.shares = shares;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public int getShares() {
        return shares;
    }

    public Stock getStock() {
        return stock;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;

    private int shares;
}