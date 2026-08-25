package com.cemal.trading_platform.repository;

import com.cemal.trading_platform.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {
}