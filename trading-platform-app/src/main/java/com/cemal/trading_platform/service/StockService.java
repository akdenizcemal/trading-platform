package com.cemal.trading_platform.service;

import com.cemal.trading_platform.model.Stock;
import com.cemal.trading_platform.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {
    private final StockRepository stockRepository;
    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }
    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }
    public Stock addStock(Stock stock) {
        return stockRepository.save(stock);
    }
    public void deleteStock(Long id) {
        stockRepository.deleteById(id);
    }
    public Stock updateStock(long id, Stock stock) {
        Stock existingStock = stockRepository.findById(id)
                .orElseThrow(() -> new StockNotFoundException(id));

        existingStock.setSymbol(stock.getSymbol());
        existingStock.setName(stock.getName());
        existingStock.setPrice(stock.getPrice());

        return stockRepository.save(existingStock);
    }
}