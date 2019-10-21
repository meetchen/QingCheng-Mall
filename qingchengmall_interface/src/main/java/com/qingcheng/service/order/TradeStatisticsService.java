package com.qingcheng.service.order;

import com.qingcheng.pojo.order.TradeStatistics;

import java.time.LocalDate;

public interface TradeStatisticsService {

    TradeStatistics trade(LocalDate date);


}
