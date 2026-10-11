package com.cemal.trading_platform.controller;

import com.cemal.trading_platform.dto.HoldingResponse;
import com.cemal.trading_platform.repository.HoldingRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/holdings")
public class HoldingController {

    private final HoldingRepository holdingRepository;

    public HoldingController(HoldingRepository holdingRepository) {
        this.holdingRepository = holdingRepository;
    }

    @GetMapping
    public List<HoldingResponse> getAllHoldings(
            Authentication authentication) {

        return holdingRepository.findByUserUsername(authentication.getName())
                .stream()
                .map(holding -> new HoldingResponse(
                        holding.getStock().getName(),
                        holding.getStock().getSymbol(),
                        holding.getShares(),
                        holding.getStock().getPrice(),
                        holding.getStock().getPrice()
                                .multiply(BigDecimal.valueOf(holding.getShares()))
                ))
                .toList();
    }
}
