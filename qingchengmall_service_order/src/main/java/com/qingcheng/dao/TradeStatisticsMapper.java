package com.qingcheng.dao;

import com.qingcheng.pojo.order.TradeStatistics;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.time.LocalDate;

public interface TradeStatisticsMapper extends Mapper<TradeStatistics> {


    @Select("")
    TradeStatistics trade(LocalDate date);
}
