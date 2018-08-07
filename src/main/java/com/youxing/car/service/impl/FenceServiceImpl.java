package com.youxing.car.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.youxing.car.dao.FenceDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Fence;
import com.youxing.car.service.FenceService;
import com.youxing.car.service.base.impl.BaseServiceImpl;

@Service
public class FenceServiceImpl extends BaseServiceImpl<Fence> implements FenceService{
    @Resource
    private FenceDao fenceDao;

    public BaseDao<Fence> getBaseDao() {
        return this.fenceDao;
    }

    @Override
    public List<Fence> findName() {
        return fenceDao.findName();
    }
}
