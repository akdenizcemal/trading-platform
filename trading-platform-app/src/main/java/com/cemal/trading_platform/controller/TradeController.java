package com.cemal.trading_platform.controller;

import com.cemal.trading_platform.dto.TradeRequest;
import com.cemal.trading_platform.dto.TradeResponse;
import com.cemal.trading_platform.model.Stock;
import com.cemal.trading_platform.model.Trade;
import com.cemal.trading_platform.model.TradeType;
import com.cemal.trading_platform.model.User;
import com.cemal.trading_platform.service.TradeService;
import com.cemal.trading_platform.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class TradeController {

    private final TradeService tradeService;
    private final UserRepository userRepository;

    public TradeController(
            TradeService tradeService,
            UserRepository userRepository) {
        this.tradeService = tradeService;
        this.userRepository = userRepository;
    }

    @GetMapping("/trades")
    public List<TradeResponse> getAllTrades(
            Authentication authentication) {

        return tradeService.getAllTrades(authentication.getName())
                .stream()
                .map(trade -> new TradeResponse(
                        trade.getUser().getUsername(),
                        trade.getStock().getName(),
                        trade.getStock().getSymbol(),
                        trade.getShares(),
                        trade.getPrice(),
                        trade.getTradeType()
                ))
                .toList();
    }

    @PostMapping("/trades")
    public TradeResponse addTrade(
            @Valid @RequestBody TradeRequest request,
            Authentication authentication) {

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        Trade trade = new Trade();
        trade.setShares(request.getShares());
        trade.setTradeType(request.getTradeType());

        user.setId(user.getId());

        Stock stock = new Stock();
        stock.setId(request.getStockId());

        trade.setUser(user);
        trade.setStock(stock);

        Trade savedTrade = tradeService.addTrade(trade);

        return new TradeResponse(
                savedTrade.getUser().getUsername(),
                savedTrade.getStock().getName(),
                savedTrade.getStock().getSymbol(),
                savedTrade.getShares(),
                savedTrade.getPrice(),
                savedTrade.getTradeType()
        );
    }
}