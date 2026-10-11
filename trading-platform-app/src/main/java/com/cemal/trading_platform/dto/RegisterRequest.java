
package com.cemal.trading_platform.dto;

public record RegisterRequest(
        String username,
        String email,
        String password
) {
}
