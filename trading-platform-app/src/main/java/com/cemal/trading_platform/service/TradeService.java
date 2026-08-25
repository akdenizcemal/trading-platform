package com.cemal.trading_platform.service;

import com.cemal.trading_platform.exception.TradeException;
import com.cemal.trading_platform.model.*;
import com.cemal.trading_platform.repository.HoldingRepository;
import com.cemal.trading_platform.repository.StockRepository;
import com.cemal.trading_platform.repository.TradeRepository;
import com.cemal.trading_platform.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class TradeService {

    TradeRepository tradeRepository;
    UserRepository userRepository;
    StockRepository stockRepository;
    HoldingRepository holdingRepository;
    public TradeService(TradeRepository tradeRepository, UserRepository userRepository, StockRepository stockRepository, HoldingRepository holdingRepository) {
        this.tradeRepository = tradeRepository;
        this.userRepository = userRepository;
        this.stockRepository = stockRepository;
        this.holdingRepository = holdingRepository;
    }
    public List<Trade> getAllTrades(){
        return tradeRepository.findAll();
    }
    public Trade addTrade(Trade trade) {
        User user = userRepository.findById(trade.getUser().getId())
                .orElseThrow(() -> new TradeException("Invalid user ID"));
        Stock stock = stockRepository.findById(trade.getStock().getId()).orElseThrow(() -> new TradeException("Invalid stock ID"));
        trade.setUser(user);
        trade.setStock(stock);
        if (trade.getShares() <= 0) {
            throw new TradeException("Shares should be greater than zero");
        }
        if (trade.getTradeType() == null) {
            throw new TradeException("Trade type is required");
        }
        trade.setPrice(stock.getPrice());
        BigDecimal totalCost = stock.getPrice().multiply(BigDecimal.valueOf(trade.getShares()));
        if (trade.getTradeType() == TradeType.BUY) {
            if (user.getBalance().compareTo(totalCost) < 0) {
                throw new TradeException("Insufficient funds");
            }
            Optional<Holding> holding =
                    holdingRepository.findByUserIdAndStockId(
                            user.getId(),
                            stock.getId()
                    );
            Holding userHolding = holding.orElse(null);
            if (userHolding != null) {
                userHolding.setShares(userHolding.getShares() + trade.getShares());
                holdingRepository.save(userHolding);
            }
            else {
                Holding newHolding = new Holding();
                newHolding.setUser(user);
                newHolding.setStock(stock);
                newHolding.setShares(trade.getShares());

                holdingRepository.save(newHolding);
            }

            user.setBalance(user.getBalance().subtract(totalCost));
        }

        else if(trade.getTradeType() == TradeType.SELL){

            Optional<Holding> holding =
                    holdingRepository.findByUserIdAndStockId(
                            user.getId(),
                            stock.getId()
                    );
            Holding userHolding = holding.orElse(null);
            if (userHolding != null) {
                if (userHolding.getShares() < trade.getShares()) {
                    throw new TradeException("Not enough shares");
                }
                    userHolding.setShares(userHolding.getShares() - trade.getShares());
                if (userHolding.getShares() == 0) {
                    holdingRepository.delete(userHolding);
                } else {
                    holdingRepository.save(userHolding);
                }


            }
            else  {
               throw new TradeException("Not owned");
            }
            user.setBalance(user.getBalance().add(totalCost));

        }
       userRepository.save(user);
       return  tradeRepository.save(trade);

    }

}
