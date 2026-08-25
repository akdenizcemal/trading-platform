package com.cemal.trading_platform.repository;

import com.cemal.trading_platform.model.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<Trade, Long> {

}
