
package com.cemal.trading_platform;

import com.cemal.trading_platform.model.Stock;
import com.cemal.trading_platform.model.Trade;
import com.cemal.trading_platform.model.User;
import com.cemal.trading_platform.repository.HoldingRepository;
import com.cemal.trading_platform.repository.StockRepository;
import com.cemal.trading_platform.repository.TradeRepository;
import com.cemal.trading_platform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class TradeControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private HoldingRepository holdingRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;
    private User user;
    private Stock stock;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .defaultRequest(
                        get("/")
                                .with(user("testuser"))
                )
                .build();

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
    void addTradeThroughController() throws Exception {
        String json = """
                {
                    "userId": %d,
                    "stockId": %d,
                    "shares": 2,
                    "tradeType": "BUY"
                }
                """.formatted(user.getId(), stock.getId());

        mockMvc.perform(post("/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shares").value(2))
                .andExpect(jsonPath("$.tradeType").value("BUY"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.stockName").value("Test Stock"))
                .andExpect(jsonPath("$.symbol").value("TEST"));
    }

    @Test
    void rejectTradeWithZeroShares() throws Exception {
        String json = """
                {
                    "userId": %d,
                    "stockId": %d,
                    "shares": 0,
                    "tradeType": "BUY"
                }
                """.formatted(user.getId(), stock.getId());

        mockMvc.perform(post("/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectTradeWithNegativeShares() throws Exception {
        String json = """
                {
                    "userId": %d,
                    "stockId": %d,
                    "shares": -1,
                    "tradeType": "BUY"
                }
                """.formatted(user.getId(), stock.getId());

        mockMvc.perform(post("/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectTradeWithMissingTradeType() throws Exception {
        String json = """
                {
                    "userId": %d,
                    "stockId": %d,
                    "shares": 5
                }
                """.formatted(user.getId(), stock.getId());

        mockMvc.perform(post("/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectTradeWithInvalidUserId() throws Exception {
        User anotherUser = new User();
        anotherUser.setUsername("anotheruser");
        anotherUser.setEmail("another@test.com");
        anotherUser.setPassword(passwordEncoder.encode("password"));
        anotherUser.setBalance(new BigDecimal("10000.00"));
        anotherUser = userRepository.save(anotherUser);

        String json = """
                {
                    "userId": %d,
                    "stockId": %d,
                    "shares": 1,
                    "tradeType": "BUY"
                }
                """.formatted(anotherUser.getId(), stock.getId());

        mockMvc.perform(post("/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));

        Trade savedTrade = tradeRepository.findByUserUsername("testuser")
                .stream()
                .findFirst()
                .orElseThrow();

        org.junit.jupiter.api.Assertions.assertEquals(
                "testuser",
                savedTrade.getUser().getUsername()
        );

        org.junit.jupiter.api.Assertions.assertTrue(
                tradeRepository.findByUserUsername("anotheruser").isEmpty()
        );
    }

    @Test
    void rejectTradeWithInvalidStockId() throws Exception {
        String json = """
                {
                    "userId": %d,
                    "stockId": 123123,
                    "shares": 1,
                    "tradeType": "BUY"
                }
                """.formatted(user.getId());

        mockMvc.perform(post("/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void insufficientFunds() throws Exception {
        String json = """
                {
                    "userId": %d,
                    "stockId": %d,
                    "shares": 100,
                    "tradeType": "BUY"
                }
                """.formatted(user.getId(), stock.getId());

        mockMvc.perform(post("/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void stockNotOwned() throws Exception {
        String json = """
                {
                    "userId": %d,
                    "stockId": %d,
                    "shares": 1,
                    "tradeType": "SELL"
                }
                """.formatted(user.getId(), stock.getId());

        mockMvc.perform(post("/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllTrades() throws Exception {
        String json = """
                {
                    "userId": %d,
                    "stockId": %d,
                    "shares": 1,
                    "tradeType": "BUY"
                }
                """.formatted(user.getId(), stock.getId());

        mockMvc.perform(post("/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        mockMvc.perform(get("/trades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].stockName").value("Test Stock"))
                .andExpect(jsonPath("$[0].symbol").value("TEST"))
                .andExpect(jsonPath("$[0].shares").value(1))
                .andExpect(jsonPath("$[0].price").value(200.00))
                .andExpect(jsonPath("$[0].tradeType").value("BUY"));
    }

    @Test
    void getAllTradesWhenNoTradesExist() throws Exception {
        mockMvc.perform(get("/trades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAllTradesWhenMultipleTradesExist() throws Exception {
        String firstJson = """
                {
                    "userId": %d,
                    "stockId": %d,
                    "shares": 1,
                    "tradeType": "BUY"
                }
                """.formatted(user.getId(), stock.getId());

        mockMvc.perform(post("/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstJson))
                .andExpect(status().isOk());

        String secondJson = """
                {
                    "userId": %d,
                    "stockId": %d,
                    "shares": 2,
                    "tradeType": "BUY"
                }
                """.formatted(user.getId(), stock.getId());

        mockMvc.perform(post("/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondJson))
                .andExpect(status().isOk());

        mockMvc.perform(get("/trades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].shares").value(1))
                .andExpect(jsonPath("$[0].tradeType").value("BUY"))
                .andExpect(jsonPath("$[1].shares").value(2))
                .andExpect(jsonPath("$[1].tradeType").value("BUY"));
    }

    @Test
    void getHoldingsAfterBuyingStock() throws Exception {
        String json = """
                {
                    "userId": %d,
                    "stockId": %d,
                    "shares": 5,
                    "tradeType": "BUY"
                }
                """.formatted(user.getId(), stock.getId());

        mockMvc.perform(post("/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        mockMvc.perform(get("/holdings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stockName").value("Test Stock"))
                .andExpect(jsonPath("$[0].symbol").value("TEST"))
                .andExpect(jsonPath("$[0].shares").value(5))
                .andExpect(jsonPath("$[0].currentPrice").value(200.00))
                .andExpect(jsonPath("$[0].currentValue").value(1000.00));
    }

    @Test
    void getHoldingsWhenNoHoldingsExist() throws Exception {
        mockMvc.perform(get("/holdings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getHoldingsWhenMultipleHoldingsExist() throws Exception {
        Stock secondStock = new Stock();
        secondStock.setName("Second Stock");
        secondStock.setSymbol("SECOND");
        secondStock.setPrice(new BigDecimal("100.00"));
        secondStock = stockRepository.save(secondStock);

        String firstJson = """
                {
                    "userId": %d,
                    "stockId": %d,
                    "shares": 2,
                    "tradeType": "BUY"
                }
                """.formatted(user.getId(), stock.getId());

        mockMvc.perform(post("/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstJson))
                .andExpect(status().isOk());

        String secondJson = """
                {
                    "userId": %d,
                    "stockId": %d,
                    "shares": 3,
                    "tradeType": "BUY"
                }
                """.formatted(user.getId(), secondStock.getId());

        mockMvc.perform(post("/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondJson))
                .andExpect(status().isOk());

        mockMvc.perform(get("/holdings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].stockName").value("Test Stock"))
                .andExpect(jsonPath("$[0].shares").value(2))
                .andExpect(jsonPath("$[1].stockName").value("Second Stock"))
                .andExpect(jsonPath("$[1].shares").value(3));
    }

    @Test
    void sellStockAndCheckHolding() throws Exception {
        String buyJson = """
                {
                    "userId": %d,
                    "stockId": %d,
                    "shares": 5,
                    "tradeType": "BUY"
                }
                """.formatted(user.getId(), stock.getId());

        mockMvc.perform(post("/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buyJson))
                .andExpect(status().isOk());

        String sellJson = """
                {
                    "userId": %d,
                    "stockId": %d,
                    "shares": 2,
                    "tradeType": "SELL"
                }
                """.formatted(user.getId(), stock.getId());

        mockMvc.perform(post("/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sellJson))
                .andExpect(status().isOk());

        mockMvc.perform(get("/holdings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stockName").value("Test Stock"))
                .andExpect(jsonPath("$[0].symbol").value("TEST"))
                .andExpect(jsonPath("$[0].shares").value(3));
    }

    @Test
    void rejectTradeWithMissingUserId() throws Exception {
        String json = """
                {
                    "stockId": %d,
                    "shares": 1,
                    "tradeType": "BUY"
                }
                """.formatted(stock.getId());

        mockMvc.perform(post("/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectTradeWithMissingStockId() throws Exception {
        String json = """
                {
                    "userId": %d,
                    "shares": 1,
                    "tradeType": "BUY"
                }
                """.formatted(user.getId());

        mockMvc.perform(post("/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectSellingMoreSharesThanOwned() throws Exception {
        String buyJson = """
                {
                    "userId": %d,
                    "stockId": %d,
                    "shares": 5,
                    "tradeType": "BUY"
                }
                """.formatted(user.getId(), stock.getId());

        mockMvc.perform(post("/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buyJson))
                .andExpect(status().isOk());

        String sellJson = """
                {
                    "userId": %d,
                    "stockId": %d,
                    "shares": 6,
                    "tradeType": "SELL"
                }
                """.formatted(user.getId(), stock.getId());

        mockMvc.perform(post("/trades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sellJson))
                .andExpect(status().isBadRequest());
    }
}
