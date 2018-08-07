package com.youxing.car.service;

import com.youxing.car.entity.Speeding;
import com.youxing.car.service.base.BaseService;

import java.util.List;

public interface SpeedingService extends BaseService<Speeding> {
    public List<Speeding> findName();
}
