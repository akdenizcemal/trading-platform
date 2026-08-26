package com.cemal.trading_platform.repository;

import com.cemal.trading_platform.model.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    List<Trade> findByUserUsername(String username);

}