package com.youxing.car.dao;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Fence;

import java.util.List;

public interface FenceDao extends BaseDao<Fence> {
    List<Fence> findName();
}
