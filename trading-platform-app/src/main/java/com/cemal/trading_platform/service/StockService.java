
package com.cemal.trading_platform.service;

import com.cemal.trading_platform.exception.TradeException;
import com.cemal.trading_platform.model.Stock;
import com.cemal.trading_platform.repository.StockRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void deleteStock(Long id) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new StockNotFoundException(id));

        try {
            stockRepository.delete(stock);
            stockRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new TradeException(
                    "Cannot delete stock because it has existing trade records"
            );
        }
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
