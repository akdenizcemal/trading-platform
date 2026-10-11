
package com.cemal.trading_platform.controller;

import com.cemal.trading_platform.model.Stock;
import com.cemal.trading_platform.service.StockService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/stocks")
    public List<Stock> getAllStocks() {
        return stockService.getAllStocks();
    }

    @PostMapping("/stocks")
    public Stock addStock(@Valid @RequestBody Stock stock) {
        return stockService.addStock(stock);
    }

    @DeleteMapping("/stocks/{id}")
    public void deleteStock(@PathVariable Long id) {
        stockService.deleteStock(id);
    }

    @PutMapping("/stocks/{id}")
    public Stock updateStock(
            @PathVariable Long id,
            @Valid @RequestBody Stock stock) {
        return stockService.updateStock(id, stock);
    }
}
