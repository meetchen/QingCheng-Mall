package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.dao.TradeStatisticsMapper;
import com.qingcheng.pojo.order.TradeStatistics;
import com.qingcheng.service.order.TradeStatisticsService;

import java.time.LocalDate;
@Service
public class TradeStatisticsServiceImpl implements TradeStatisticsService {

    private TradeStatisticsMapper tradeStatisticsMapper;
    @Override
    public TradeStatistics trade(LocalDate date) {
        return null;
    }
}
