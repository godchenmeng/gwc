package com.youxing.car.dao;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Speeding;

import java.util.List;

public interface SpeedingDao extends BaseDao<Speeding> {
    public List<Speeding> findName();
}
