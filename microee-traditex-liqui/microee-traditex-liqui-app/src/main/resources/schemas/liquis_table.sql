
CREATE TABLE IF NOT EXISTS `t_order_disk_detail` (
  `disk_detail_id` varchar(68) NOT NULL COMMENT '主键',
  `disk_client_order_id` varchar(48) NOT NULL COMMENT '摆盘挂单id主键',
  `disk_result_order_id` varchar(48) NOT NULL COMMENT '挂单成功后返回的id',
  `match_id` varchar(36) DEFAULT NULL COMMENT '撮合id',
  `vender` varchar(15) NOT NULL COMMENT '供应商, 用于标识该比订单发送到了哪个交易所',
  `disk_order_symbol` varchar(15) NOT NULL COMMENT '交易对',
  `disk_filled_amount` decimal(36,18) DEFAULT NULL COMMENT '成交数量',
  `disk_filled_price` decimal(36,18) DEFAULT NULL COMMENT '成交单价',
  `filled_cash_amount` decimal(36,18) DEFAULT NULL COMMENT '实际支付价格',
  `disk_order_state` varchar(25) NOT NULL COMMENT '订单状态',
  `disk_order_type` varchar(25) NOT NULL COMMENT '订单类型',
  `unfilled_amount` decimal(36,18) DEFAULT NULL COMMENT '未成交数量',
  `emitter_time` timestamp(3) NULL DEFAULT NULL COMMENT '交易所的订单创建时间',
  `created_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '保存到数据库的时间',
  PRIMARY KEY (`disk_detail_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='摆盘挂单订单详情表';

CREATE TABLE IF NOT EXISTS `t_order_disk_table` (
  `disk_client_order_id` varchar(48) NOT NULL COMMENT '摆盘挂单id主键',
  `disk_result_order_id` varchar(48) NOT NULL COMMENT '挂单成功后返回的id',
  `order_book_id` varchar(52) NOT NULL COMMENT '该比订单用的是哪个orderbook',
  `vender` varchar(15) NOT NULL COMMENT '供应商, 用于标识该比订单发送到了哪个交易所',
  `target_symbol` varchar(15) NOT NULL COMMENT '目标交易对',
  `target_side` char(6) NOT NULL COMMENT '买卖方向',
  `target_amount` decimal(36,18) NOT NULL COMMENT '目标数量',
  `target_price` decimal(36,18) NOT NULL COMMENT '目标价格',
  `usdt_price` decimal(36,18) DEFAULT NULL COMMENT 'usdt价格',
  `usd_currency_pricing` decimal(36,18) NOT NULL COMMENT '当前计价货币和美元的汇率(此汇率可能来自Oanda)',
  `usdt_usd_rate` decimal(36,18) NOT NULL COMMENT 'usdt和usd的价格比率(此比率可能在后台配置也可能从第三方获取)',
  `disk_amount` decimal(36,18) NOT NULL COMMENT '挂单实际数量',
  `disk_price` decimal(36,18) NOT NULL COMMENT '实际挂单价格',
  `disk_price_prec` int(2) NOT NULL COMMENT '挂单价格精度',
  `disk_amount_prec` int(2) NOT NULL COMMENT '挂单数量精度',
  `disk_order_type` varchar(22) NOT NULL COMMENT '挂单订单类型',
  `disk_account` varchar(48) NOT NULL COMMENT '挂单账户',
  `disk_order_result` varchar(412) DEFAULT NULL COMMENT '挂单后返回的结果，超时的情况下可能是null',
  `disk_ocst` int(2) unsigned NOT NULL COMMENT 'order_create_spend_time 创建订单耗时',
  `created_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '该比订单的创建时间',
  PRIMARY KEY (`disk_client_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='挂单表';

CREATE TABLE IF NOT EXISTS `t_order_revoke_record` (
  `disk_order_revoke_id` varchar(48) NOT NULL COMMENT '撤单id主键',
  `disk_result_order_id` varchar(48) NOT NULL COMMENT '挂单成功后返回的id',
  `vender` varchar(15) NOT NULL COMMENT '供应商, 用于标识该比订单发送到了哪个交易所',
  `revoke_orst` int(2) unsigned NOT NULL COMMENT 'order_revoke_spend_time 撤单耗时',
  `start_time` timestamp(3) default NULL COMMENT '开始时间',
  `end_time` timestamp(3) default NULL COMMENT '结束时间',
  `created_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '保存时间',
  `error_code` varchar(35) DEFAULT NULL COMMENT '错误码',
  `error_message` varchar(128) DEFAULT NULL COMMENT '错误消息',
  `order_book_id` varchar(52) NOT NULL COMMENT '该比订单用的是哪个orderbook',
  PRIMARY KEY (`disk_order_revoke_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='撤单表';

CREATE TABLE IF NOT EXISTS `t_order_solr_detail` (
  `solr_detail_id` varchar(68) NOT NULL COMMENT '主键',
  `solr_client_order_id` varchar(62) NOT NULL COMMENT '甩单对冲订单客户端id',
  `solr_result_order_id` varchar(48) NOT NULL COMMENT '甩单成功后返回的id',
  `vender` varchar(15) NOT NULL COMMENT '供应商, 用于标识该比订单发送到了哪个交易所',
  `solr_account` varchar(255) NOT NULL COMMENT '甩单账户',
  `solr_order_symbol` varchar(15) NOT NULL COMMENT '交易对',
  `solr_filled_amount` decimal(36,18) DEFAULT NULL COMMENT '成交数量',
  `solr_filled_price` decimal(36,18) DEFAULT NULL COMMENT '成交价格',
  `filled_cash_amount` decimal(36,18) DEFAULT NULL COMMENT '实际支付价格',
  `solr_order_state` varchar(25) NOT NULL COMMENT '订单状态',
  `solr_order_type` varchar(22) NOT NULL COMMENT '订单类型',
  `unfilled_amount` decimal(36,18) DEFAULT NULL COMMENT '未成交数量',
  `emitter_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '成交时间',
  `created_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '保存时间',
  PRIMARY KEY (`solr_detail_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='甩单表详情';

CREATE TABLE IF NOT EXISTS `t_order_solr_table` (
  `solr_client_order_id` varchar(62) NOT NULL COMMENT '甩单对冲订单客户端id',
  `solr_result_order_id` varchar(52) NOT NULL COMMENT '甩单成功后返回的id',
  `disk_result_order_id` varchar(52) NOT NULL COMMENT '挂单成功后返回的id',
  `disk_symbol` varchar(15) NOT NULL COMMENT '挂单交易对',
  `order_book_id` varchar(52) NOT NULL COMMENT '甩单时用到的orderbook',
  `solr_osst` int(2) unsigned NOT NULL COMMENT 'order_solr_spend_time 甩单耗时',
  `vender` varchar(15) NOT NULL COMMENT '供应商, 用于标识该比订单发送到了哪个交易所',
  `solr_account` varchar(48) DEFAULT NULL COMMENT '账户',
  `solr_symbol` varchar(15) NOT NULL COMMENT '交易对',
  `solr_side` char(6) NOT NULL COMMENT '买卖方向',
  `solr_amount` decimal(36,18) DEFAULT NULL COMMENT '数量',
  `solr_price` decimal(36,18) DEFAULT NULL COMMENT '价格',
  `solr_amount_prec` int(2) unsigned DEFAULT NULL COMMENT '数量精度',
  `solr_price_prec` int(2) unsigned DEFAULT NULL COMMENT '价格精度',
  `solr_order_amount` decimal(36,18) DEFAULT NULL COMMENT '数量',
  `solr_order_price` decimal(36,18) DEFAULT NULL COMMENT '价格',
  `solr_order_type` varchar(25) NOT NULL COMMENT '订单类型',
  `usd_currency_pricing` decimal(36,18) NOT NULL COMMENT '当前计价货币和美元的汇率(此汇率可能来自Oanda)',
  `usdt_usd_rate` decimal(36,18) NOT NULL COMMENT 'usdt和usd的价格比率(此比率可能在后台配置也可能从第三方获取)',
  `created_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '甩单时间',
  PRIMARY KEY (`solr_client_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='甩单表';


CREATE TABLE IF NOT EXISTS `t_order_solr_next` (
  `next_solr_id` varchar(52) NOT NULL COMMENT '主键',
  `solr_client_order_id` varchar(62) NOT NULL COMMENT '甩单对冲订单客户端id',
  `solr_symbol` varchar(15) NOT NULL COMMENT '交易对',
  `solr_side` char(6) NOT NULL COMMENT '买卖方向',
  `solr_order_type` varchar(25) NOT NULL COMMENT '订单类型',
  `next_solr_quantity` decimal(36,18) DEFAULT NULL COMMENT '下一次甩的金额或数量',
  `disk_result_order_id` varchar(52) NOT NULL COMMENT '挂单成功后返回的id',
  `disk_symbol` varchar(15) NOT NULL COMMENT '挂单交易对',
  `created_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  PRIMARY KEY (`next_solr_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='甩单表';


CREATE TABLE IF NOT EXISTS `t_orderbook_stream` (
  `order_book_id` varchar(52) NOT NULL COMMENT '主键',
  `book_id` varchar(48) DEFAULT NULL COMMENT 'id',
  `symbol` varchar(15) NOT NULL COMMENT '交易对',
  `side` char(6) NOT NULL COMMENT '买卖方向: SELL/卖, BUY/买',
  `amount` decimal(36,18) NOT NULL COMMENT '数量',
  `price` decimal(36,18) NOT NULL COMMENT '金额',
  `vender` varchar(15) NOT NULL COMMENT '供应商, 用于标识该条数据来自哪个交易所',
  `cal_type` int(2) NOT NULL COMMENT '用于说明价格和数量的计算方式, 已btcusdt为例: 1/表示一个btc的价格，2/用11395.2的价格卖多少个btc',
  `pick` int(2) NOT NULL COMMENT '深度档位',
  `skip_code` int(2) NOT NULL COMMENT '是否跳过: 0/没有, 1/由于盘口有挂单所以跳过该行情, 2/由于盘口资金超过上限所以跳过，3/由于触发熔断阈值所以跳过，4/盘口未开所以跳过',
  `emitter_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '从交易所发出了的时候',
  `reveive_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '客户端收到的时间',
  `created_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '客户端存入数据库的时间',
  PRIMARY KEY (`order_book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='盘口数据表';

CREATE TABLE IF NOT EXISTS `t_order_hedging_table` (
  `order_id` varchar(48) NOT NULL COMMENT '主键',
  `disk_client_order_id` varchar(48) NOT NULL COMMENT '摆盘挂单id主键',
  `disk_match_id` varchar(48) DEFAULT NULL COMMENT '挂单成功后返回的id',
  `disk_filled_amount` varchar(54) NOT NULL COMMENT '挂单成交数量',
  `disk_trade_time` bigint(20) DEFAULT NULL COMMENT '挂单交易时间',
  `side` char(6) NOT NULL COMMENT '多空方向',
  `before_balance` decimal(36,18) NOT NULL COMMENT '下单前的余额',
  `instrument` varchar(18) NOT NULL COMMENT '对冲交易对',
  `pricing` decimal(36,18) NOT NULL COMMENT '基础货币价格',
  `unit` decimal(36,18) NOT NULL COMMENT '数量',
  `created_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '该比订单的创建时间',
  `create_order_result` varchar(2048) NOT NULL COMMENT '创建订单后的返回结果',
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='多空订单表';

CREATE TABLE IF NOT EXISTS `t_liquid_archive_record` (
  `id` varchar(48) NOT NULL COMMENT '主键',
  `the_table_name` varchar(48) NOT NULL COMMENT '表名',
  `last_id` varchar(48) DEFAULT NULL COMMENT '最后一个已归档的主键id',
  `rows_count` int(4) NOT NULL COMMENT '本次归档的数据行数',
  `last_timestamp` timestamp(3) NULL DEFAULT NULL COMMENT '最后一次归档时间',
  `file_id` varchar(48) NOT NULL COMMENT '归档文件的id',
  `file_url` varchar(1148) NOT NULL COMMENT '归档文件的下载地址',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='历史数据归档';

CREATE TABLE IF NOT EXISTS `t_liquidity_alarm_settings` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `strategy_id` bigint(20) unsigned NOT NULL COMMENT '流动性策略配置表关联id',
  `liquidity_symbol` varchar(30) NOT NULL COMMENT '流动性交易对 例如btc/jpy',
  `disk_base` varchar(10) NOT NULL COMMENT '盘面对冲交易对base 例如btc',
  `disk_quote` varchar(10) NOT NULL COMMENT '盘面对冲交易对quote 例如usdt',
  `exchange_base` varchar(10) NOT NULL COMMENT '汇率对冲交易对base 例如usd',
  `exchange_quote` varchar(10) NOT NULL COMMENT '汇率对冲交易对quote 例如jpy',
  `liquidity_warn_disk_base_pos` varchar(30) NOT NULL COMMENT '流动性账户对冲交易对base仓位 例如btc',
  `liquidity_warn_exchange_quote_pos` varchar(30) NOT NULL COMMENT '流动性账户汇率对冲交易对quote 例如jpy',
  `liquidity_warn_network_delay` varchar(30) NOT NULL COMMENT '流动性账户网络延迟',
  `disk_warn_disk_base_pos` varchar(30) NOT NULL COMMENT '盘面账户对冲交易对base仓位 例如btc',
  `disk_warn_disk_quote_pos` varchar(30) NOT NULL COMMENT '盘面账户汇率对冲交易对quote 例如usdt',
  `disk_warn_network_delay` varchar(30) NOT NULL COMMENT '盘面账户网络延迟',
  `exchange_warn_exchange_base_pos` varchar(30) NOT NULL COMMENT '汇率对冲账户对冲交易对base仓位 例如usd',
  `exchange_warn_exchange_quote_pos` varchar(30) NOT NULL COMMENT '汇率对冲账户汇率对冲交易对quote 例如jpy',
  `exchange_warn_network_delay` varchar(30) NOT NULL COMMENT '汇率对冲账户网络延迟',
  `pnl_warn_day_total` varchar(30) NOT NULL COMMENT '一日损益值',
  `pnl_warn_week_total` varchar(30) NOT NULL COMMENT '一周损益值',
  `pnl_warn_month_total` varchar(30) NOT NULL COMMENT '一月损益值',
  `liquidity_fuse_disk_base_pos` varchar(30) NOT NULL COMMENT '流动性账户对冲交易对base仓位 例如btc',
  `liquidity_fuse_exchange_quote_pos` varchar(30) NOT NULL COMMENT '流动性账户汇率对冲交易对quote 例如jpy',
  `liquidity_fuse_network_delay` varchar(30) NOT NULL COMMENT '流动性账户网络延迟',
  `disk_fuse_disk_base_pos` varchar(30) NOT NULL COMMENT '盘面账户对冲交易对base仓位 例如btc',
  `disk_fuse_disk_quote_pos` varchar(30) NOT NULL COMMENT '盘面账户汇率对冲交易对quote 例如usdt',
  `disk_fuse_network_delay` varchar(30) NOT NULL COMMENT '盘面账户网络延迟',
  `exchange_fuse_exchange_base_pos` varchar(30) NOT NULL COMMENT '汇率对冲账户对冲交易对base仓位 例如usd',
  `exchange_fuse_exchange_quote_pos` varchar(30) NOT NULL COMMENT '汇率对冲账户汇率对冲交易对quote 例如jpy',
  `exchange_fuse_network_delay` varchar(30) NOT NULL COMMENT '汇率对冲账户网络延迟',
  `pnl_fuse_day_total` varchar(30) NOT NULL COMMENT '一日损益值',
  `pnl_fuse_week_total` varchar(30) NOT NULL COMMENT '一周损益值',
  `pnl_fuse_month_total` varchar(30) NOT NULL COMMENT '一月损益值',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  `operator_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '操作人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='流动性策略报警配置表';

CREATE TABLE IF NOT EXISTS `t_liquidity_strategy_settings` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `liquidity_symbol` varchar(30) NOT NULL COMMENT '流动性交易对 例如btc/jpy',
  `disk_hedge_symbol` varchar(30) NOT NULL COMMENT '盘面对冲交易对 例如btc/usdt',
  `exchange_hedge_symbol` varchar(30) NOT NULL COMMENT '汇率对冲交易对 例如usd/jpy',
  `disk_base` varchar(10) NOT NULL COMMENT '盘面对冲交易对base 例如btc',
  `disk_quote` varchar(10) NOT NULL COMMENT '盘面对冲交易对quote 例如usdt',
  `exchange_base` varchar(10) NOT NULL COMMENT '汇率对冲交易对base 例如usd',
  `exchange_quote` varchar(10) NOT NULL COMMENT '汇率对冲交易对quote 例如jpy',
  `buy_state` int(2) unsigned DEFAULT NULL COMMENT '买盘状态, 1-开启, 2-关闭',
  `sell_state` int(2) unsigned DEFAULT NULL COMMENT '卖盘状态, 1-开启, 2-关闭',
  `depth_step` int(2) unsigned DEFAULT NULL COMMENT '聚合档位',
  `buy_money_limit` varchar(30) DEFAULT NULL COMMENT '买盘占用资金上限',
  `sell_money_limit` varchar(30) DEFAULT NULL COMMENT '卖盘占用资金上限',
  `price_precson` int(2) unsigned DEFAULT NULL COMMENT '摆单价格精度',
  `quantity_precson` int(2) unsigned DEFAULT NULL COMMENT '摆单数量精度',
  `buy_price_incr_rate` text COMMENT '买盘挂单价格配置[{ id:1, price: 0.3, quantity: 0.1}]',
  `sell_price_incr_rate` text COMMENT '卖盘挂单价格配置[{ id:1, price: 0.3, quantity: 0.1}]',
  `disk_buy_precson` int(2) unsigned DEFAULT NULL COMMENT '盘面对冲买单精度',
  `disk_sell_precson` int(2) unsigned DEFAULT NULL COMMENT '盘面对冲卖单精度',
  `disk_price_scale_max` varchar(30) DEFAULT NULL COMMENT '盘面对冲价格比例上限最大值',
  `disk_price_scale_min` varchar(30) DEFAULT NULL COMMENT '盘面对冲价格比例上限最小值',
  `exchange_buy_price_precson` int(2) unsigned DEFAULT NULL COMMENT '对冲买单精度',
  `exchange_sell_price_precson` int(2) unsigned DEFAULT NULL COMMENT '汇率对冲卖单精度',
  `exchange_config` varchar(30) DEFAULT NULL COMMENT '汇率配置 例如 1usdt=？usd disk_quote exchange_base',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  `operator_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '操作人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8 COMMENT='流动性策略配置表';


--alter table `t_order_disk_detail` add column `match_id` varchar(36) DEFAULT NULL COMMENT '撮合id' after `disk_result_order_id`;
--alter table `t_orderbook_stream` add column `book_id` varchar(48) DEFAULT NULL COMMENT 'id' after `order_book_id`;

--alter table `t_order_solr_table` add column `order_book_id` varchar(52) DEFAULT NULL COMMENT '甩单时用到的orderbook' after `disk_symbol`;
--alter table `t_orderbook_stream` modify column `amount` varchar(54) DEFAULT NULL COMMENT '数量';
--alter table `t_orderbook_stream` modify column `price` varchar(54) DEFAULT NULL COMMENT '金额';


