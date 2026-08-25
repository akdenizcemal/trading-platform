
        package com.cemal.trading_platform;

import com.cemal.trading_platform.exception.TradeException;
import com.cemal.trading_platform.model.*;
import com.cemal.trading_platform.repository.HoldingRepository;
import com.cemal.trading_platform.repository.StockRepository;
import com.cemal.trading_platform.repository.TradeRepository;
import com.cemal.trading_platform.repository.UserRepository;
import com.cemal.trading_platform.service.TradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TradeIntegrationTest {

    User user;
    Stock stock;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StockRepository stockRepository;

    @Autowired
    HoldingRepository holdingRepository;

    @Autowired
    TradeRepository tradeRepository;

    @Autowired
    TradeService tradeService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanDatabase() {
        tradeRepository.deleteAll();
        holdingRepository.deleteAll();
        userRepository.deleteAll();
        stockRepository.deleteAll();

        user = new User();
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setBalance(new BigDecimal("10000.00"));

        user = userRepository.save(user);

        stock = new Stock();
        stock.setSymbol("TEST");
        stock.setName("Test Stock");
        stock.setPrice(new BigDecimal("200.00"));

        stock = stockRepository.save(stock);
    }

    @Test
    void buyStock() {
        Trade trade = new Trade();

        trade.setUser(user);
        trade.setStock(stock);
        trade.setShares(2);
        trade.setTradeType(TradeType.BUY);

        Trade savedTrade = tradeService.addTrade(trade);

        assertNotNull(savedTrade.getId());
        assertEquals(2, savedTrade.getShares());
        assertEquals(TradeType.BUY, savedTrade.getTradeType());
        assertEquals(stock.getId(), savedTrade.getStock().getId());
        assertEquals(user.getId(), savedTrade.getUser().getId());

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();

        assertEquals(new BigDecimal("9600.00"), updatedUser.getBalance());

        Holding holding = holdingRepository
                .findByUserIdAndStockId(user.getId(), stock.getId())
                .orElseThrow();

        assertEquals(2, holding.getShares());
        assertEquals(user.getId(), holding.getUser().getId());
        assertEquals(stock.getId(), holding.getStock().getId());
    }

    @Test
    void buyMoreOfSameStock() {
        Trade firstTrade = new Trade();

        firstTrade.setUser(user);
        firstTrade.setStock(stock);
        firstTrade.setShares(2);
        firstTrade.setTradeType(TradeType.BUY);

        tradeService.addTrade(firstTrade);

        Trade secondTrade = new Trade();

        secondTrade.setUser(user);
        secondTrade.setStock(stock);
        secondTrade.setShares(3);
        secondTrade.setTradeType(TradeType.BUY);

        tradeService.addTrade(secondTrade);

        Holding holding = holdingRepository
                .findByUserIdAndStockId(user.getId(), stock.getId())
                .orElseThrow();

        assertEquals(5, holding.getShares());
    }

    @Test
    void sellStock() {
        Trade firstTrade = new Trade();

        firstTrade.setUser(user);
        firstTrade.setStock(stock);
        firstTrade.setShares(5);
        firstTrade.setTradeType(TradeType.BUY);

        tradeService.addTrade(firstTrade);

        Trade trade = new Trade();

        trade.setUser(user);
        trade.setStock(stock);
        trade.setShares(2);
        trade.setTradeType(TradeType.SELL);

        tradeService.addTrade(trade);

        Holding holding = holdingRepository
                .findByUserIdAndStockId(user.getId(), stock.getId())
                .orElseThrow();

        assertEquals(3, holding.getShares());

        User updatedUser = userRepository
                .findById(user.getId())
                .orElseThrow();

        assertEquals(new BigDecimal("9400.00"), updatedUser.getBalance());
    }

    @Test
    void errorWhenSellingStockMoreThanOwned() {
        Trade firstTrade = new Trade();

        firstTrade.setUser(user);
        firstTrade.setStock(stock);
        firstTrade.setShares(5);
        firstTrade.setTradeType(TradeType.BUY);

        tradeService.addTrade(firstTrade);

        Trade trade = new Trade();

        trade.setUser(user);
        trade.setStock(stock);
        trade.setShares(8);
        trade.setTradeType(TradeType.SELL);

        assertThrows(
                TradeException.class,
                () -> tradeService.addTrade(trade)
        );
    }

    @Test
    void sellAllShares() {
        Trade firstTrade = new Trade();

        firstTrade.setUser(user);
        firstTrade.setStock(stock);
        firstTrade.setShares(5);
        firstTrade.setTradeType(TradeType.BUY);

        tradeService.addTrade(firstTrade);

        Trade trade = new Trade();

        trade.setUser(user);
        trade.setStock(stock);
        trade.setShares(5);
        trade.setTradeType(TradeType.SELL);

        tradeService.addTrade(trade);

        assertThrows(
                NoSuchElementException.class,
                () -> holdingRepository
                        .findByUserIdAndStockId(user.getId(), stock.getId())
                        .orElseThrow()
        );
    }

    @Test
    void errorWhenInsufficientFunds() {
        Trade firstTrade = new Trade();

        firstTrade.setUser(user);
        firstTrade.setStock(stock);
        firstTrade.setShares(52);
        firstTrade.setTradeType(TradeType.BUY);

        assertThrows(
                TradeException.class,
                () -> tradeService.addTrade(firstTrade)
        );

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();

        assertEquals(
                new BigDecimal("10000.00"),
                updatedUser.getBalance()
        );

        assertThrows(
                NoSuchElementException.class,
                () -> holdingRepository
                        .findByUserIdAndStockId(user.getId(), stock.getId())
                        .orElseThrow()
        );
    }

    @Test
    void errorWhenSharesAreZero() {
        Trade firstTrade = new Trade();

        firstTrade.setUser(user);
        firstTrade.setStock(stock);
        firstTrade.setShares(0);
        firstTrade.setTradeType(TradeType.BUY);

        assertThrows(
                TradeException.class,
                () -> tradeService.addTrade(firstTrade)
        );
    }

    @Test
    void errorWhenSharesAreNegative() {
        Trade firstTrade = new Trade();

        firstTrade.setUser(user);
        firstTrade.setStock(stock);
        firstTrade.setShares(-1);
        firstTrade.setTradeType(TradeType.BUY);

        assertThrows(
                TradeException.class,
                () -> tradeService.addTrade(firstTrade)
        );
    }

    @Test
    void errorWhenInvalidId() {
        User newUser = new User();
        newUser.setId(34L);

        Trade firstTrade = new Trade();

        firstTrade.setUser(newUser);
        firstTrade.setStock(stock);
        firstTrade.setShares(5);
        firstTrade.setTradeType(TradeType.BUY);

        assertThrows(
                TradeException.class,
                () -> tradeService.addTrade(firstTrade)
        );
    }

    @Test
    void errorWhenInvalidStockId() {
        Stock newStock = new Stock();
        newStock.setId(34L);

        Trade firstTrade = new Trade();

        firstTrade.setUser(user);
        firstTrade.setStock(newStock);
        firstTrade.setShares(5);
        firstTrade.setTradeType(TradeType.BUY);

        assertThrows(
                TradeException.class,
                () -> tradeService.addTrade(firstTrade)
        );
    }

    @Test
    void errorWhenTradeTypeIsNull() {
        Trade firstTrade = new Trade();

        firstTrade.setUser(user);
        firstTrade.setStock(stock);
        firstTrade.setShares(5);

        assertThrows(
                TradeException.class,
                () -> tradeService.addTrade(firstTrade)
        );
    }

    @Test
    void errorWhenSellingStockNotOwned() {
        Trade firstTrade = new Trade();

        firstTrade.setUser(user);
        firstTrade.setStock(stock);
        firstTrade.setShares(5);
        firstTrade.setTradeType(TradeType.SELL);

        assertThrows(
                TradeException.class,
                () -> tradeService.addTrade(firstTrade)
        );
    }
}

