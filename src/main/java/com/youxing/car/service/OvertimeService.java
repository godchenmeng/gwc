package com.youxing.car.service;

import com.youxing.car.entity.OverTime;
import com.youxing.car.service.base.BaseService;

import java.util.List;

public interface OvertimeService extends BaseService<OverTime>{
    public void setRead(List<Long> ids);
}
