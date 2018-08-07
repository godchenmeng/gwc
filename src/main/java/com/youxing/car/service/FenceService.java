package com.youxing.car.service;

import com.youxing.car.entity.Fence;
import com.youxing.car.service.base.BaseService;

import java.util.List;

public interface FenceService extends BaseService<Fence> {
    List<Fence> findName();
}
