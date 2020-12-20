package com.microee.traditex.liqui.app.constrants;

public class LiquiPrefixs {

    // 交易对 btcusdt
    public static final String SYMBOL_BTC_USDT = "btcusdt";

    // 所有挂出去的订单队列
    public static final String LIQUI_ORDER_BOOK = "__liqui_order_book";
    
    // 所有挂出去的订单队列
    public static final String DISK_ORDER_PREFIX = "__liqui_disk_orders:list";
    
    // 盘口挂单锁
    public static final String DISK_ORDER_LOCKERED_PREFIX = "__liqui_disk_lockeredd_orders:list";
    
    // 撤单计数统计排名
    public static final String DISK_ORDER_REVOKE_SAORE_PREFIX = "__liqui_disk_orders_revoke:score"; 
    
    // 外汇价格缓存
    // __liqui_price-stream:buy:USDJPY      106.032
    // __liqui_price-stream:sell:USDJPY     106.036
    public static final String PRICING_STREAM_PREFIX = "__liqui_price-stream:"; 
    
    // 账户余额缓存
    public static final String ACCOUNT_BALANCE_DISK_PREFIX = "__liqui_account-balance:disk"; // 可用余额
    public static final String ACCOUNT_BALANCE_SOLR_PREFIX = "__liqui_account-balance:solr"; // 可用余额
    public static final String ACCOUNT_FROZEN_DISK_PREFIX = "__liqui_account-frozen:disk"; // 冻结余额
    public static final String ACCOUNT_BALANCE_AND_FROZEN_DISK_PREFIX = "__liqui_account-balance-and-frozen:disk:"; // 可用and冻结余额
    public static final String ACCOUNT_BALANCE_AND_FROZEN_SOLR_PREFIX = "__liqui_account-balance-and-frozen:solr:"; // 可用and冻结余额
    
    // 甩单重试次数计数
    public static final String SOLR_ORDER_RETRY_COUNT_PREFIX = "__liqui_solr_order_retry_count:score"; 
    
    // 流动性摆盘策略配置
    public static final String LIQUI_STRATEGY_SETTINGS = "__liqui_strategy_settings:"; 
    // 流动性摆盘策略配置
    public static final String LIQUI_ALARM_SETTINGS = "__liqui_alarm_settings:"; 
    
    // 买盘资金上限
    public static final String LIQUI_DISK_BUY_MONEY_MAX_LIMIT = "__liqui_disk_buy_money_max_limit:"; 
    
    // 卖盘数量上限
    public static final String LIQUI_DISK_SELL_AMOUNT_MAX_LIMIT = "__liqui_disk_sell_amount_max_limit:"; 
    
}
