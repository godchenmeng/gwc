package com.youxing.car.service.impl;

import com.youxing.car.dao.SpeedingDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Speeding;
import com.youxing.car.service.SpeedingService;
import com.youxing.car.service.base.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SpeedingServiceImpl extends BaseServiceImpl<Speeding> implements SpeedingService {
    @Resource
    private SpeedingDao speedingDao;
    public BaseDao<Speeding> getBaseDao() {
        return this.speedingDao;
    }

    @Override
    public List<Speeding> findName() {
        return speedingDao.findName();
    }
}
