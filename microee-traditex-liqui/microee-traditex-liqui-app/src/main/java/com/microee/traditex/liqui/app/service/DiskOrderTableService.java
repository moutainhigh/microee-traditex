package com.microee.traditex.liqui.app.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.microee.traditex.inbox.oem.constrants.OrderSideEnum;
import com.microee.traditex.inbox.oem.constrants.VENDER;
import com.microee.traditex.liqui.app.mappers.DiskOrderTableMapper;
import com.microee.traditex.liqui.oem.models.DiskOrderTable;

@Service
public class DiskOrderTableService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiskOrderTableService.class);
    
    @Autowired
    private DiskOrderTableMapper diskOrderTableMapper;

    public boolean save(List<DiskOrderTable> list) {
        if (list == null || list.size() == 0) {
            return true;
        }
        try {
            diskOrderTableMapper.insertList(list.stream().filter(f -> f.getDiskClientOrderId() != null).collect(Collectors.toList()));
        } catch (Exception e) {
            LOGGER.error("DiskOrderTableService#save: errorMessage={}", e.getMessage());
        }
        return true;
    }

    public DiskOrderTable buildDiskOrder(String clientOrderId, String orderResultId, VENDER vender, String symbol,
            OrderSideEnum side, String targetAmount, String targetPrice,
            String usdCurrencyPricing, String usdtUSDRate, String diskAmount,
            String diskPrice, int diskPricePrec, int diskAmountPrec,
            String diskOrderType, String diskAccount, String curl, String diskOrderResult,
            Integer diskOcst, String orderBookId, String USDT_PRICE) {
        if (clientOrderId == null) {
            return null;
        }
        DiskOrderTable diskOrder = new DiskOrderTable();
        diskOrder.setDiskClientOrderId(clientOrderId);
        diskOrder.setDiskResultOrderId(orderResultId);
        diskOrder.setVender(vender.code);
        diskOrder.setTargetSymbol(symbol);
        diskOrder.setTargetSide(side.name());
        diskOrder.setTargetAmount(new BigDecimal(targetAmount));   
        diskOrder.setTargetPrice(new BigDecimal(targetPrice));
        diskOrder.setUsdCurrencyPricing(new BigDecimal(usdCurrencyPricing));
        diskOrder.setUsdtUsdRate(new BigDecimal(usdtUSDRate));
        diskOrder.setDiskAmount(new BigDecimal(diskAmount));
        diskOrder.setDiskPrice(new BigDecimal(diskPrice));
        diskOrder.setDiskPricePrec(diskPricePrec);
        diskOrder.setDiskAmountPrec(diskAmountPrec);
        diskOrder.setDiskOrderType(diskOrderType);
        diskOrder.setDiskAccount(diskAccount);
        diskOrder.setDiskOrderResult(diskOrderResult);
        diskOrder.setDiskOcst(diskOcst);
        diskOrder.setCreatedAt(new Date(Instant.now().toEpochMilli()));
        diskOrder.setOrderBookId(orderBookId);
        diskOrder.setUsdtPrice(new BigDecimal(USDT_PRICE));
        return diskOrder;
    }

}
