package com.cemal.trading_platform;

import org.aspectj.weaver.tools.cache.AsynchronousFileCacheBacking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SecurityIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void unauthenticatedUserCannotAccessTrades() throws Exception {
        mockMvc.perform(get("/trades"))
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedUserCannotAccessHoldings() throws Exception {
        mockMvc.perform(get("/holdings"))
                .andExpect(status().isForbidden());
    }

    @Test
    void authenticatedUserCanAccessTrades() throws Exception {
        mockMvc.perform(
                        get("/trades")
                                .with(user("cemal"))
                )
                .andExpect(status().isOk());
    }

    @Test
    void authenticatedUserCanAccessHoldings() throws Exception {
        mockMvc.perform(
                        get("/holdings")
                                .with(user("cemal"))
                )
                .andExpect(status().isOk());
    }


}