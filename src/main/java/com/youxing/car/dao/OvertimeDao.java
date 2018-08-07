package com.youxing.car.dao;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.OverTime;

import java.util.List;

public interface OvertimeDao extends BaseDao<OverTime>{
    public void setRead(List<Long> ids);
}
