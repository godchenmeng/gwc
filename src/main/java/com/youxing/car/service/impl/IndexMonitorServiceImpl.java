package com.youxing.car.service.impl;

import com.youxing.car.dao.ControlDao;
import com.youxing.car.dao.IndexMonitorDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.IndexMonitorCar;
import com.youxing.car.service.IndexMonitorService;
import com.youxing.car.service.base.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IndexMonitorServiceImpl  extends BaseServiceImpl<IndexMonitorCar> implements IndexMonitorService{
    @Resource
    private IndexMonitorDao indexMonitorDao;
    @Resource
    private ControlDao controlDao;
    public BaseDao<IndexMonitorCar> getBaseDao() {
        return this.indexMonitorDao;
    }
    @Override
    public List<IndexMonitorCar> getIndexMonitorCarByUid(Long id){
        return  indexMonitorDao.getIndexMonitorCarByUid(id);
    }
    @Override
    public Map<String, Object> getIndexMonitorCarCount(Map<String,Object> map){
/*        Map<String, Object> result = new HashMap<String, Object>();
        List<IndexMonitorCar> indexMonitorCars = indexMonitorDao.getIndexMonitorCarCount(map);
        int countOnlineCar = 0;
        for(int i = 0; i < indexMonitorCars.size(); i++){
            IndexMonitorCar indexMonitorCar = indexMonitorCars.get(i);
            if(indexMonitorCar.getAcc().equals("1")){
                countOnlineCar++;
            }
        }
        result.put("all_car",indexMonitorCars.size());
        result.put("online_car",countOnlineCar);
        result.put("offline_car",indexMonitorCars.size() - countOnlineCar);//离线车辆数量
        return result;*/

		Map<String, Object> result = new HashMap<String, Object>();
		List<HashMap<String, Object>> list = indexMonitorDao.getIndexMonitorCarCount(map);
		Integer offline_car = 0;
		Integer online_car = 0;
		for (HashMap<String, Object> hashMap : list) {
			String acc = hashMap.get("acc").toString();
			if (acc.equals("0")) {
				offline_car = Integer.parseInt(hashMap.get("number").toString());
			} else if (acc.equals("1") || acc.equals("2")) {
				online_car += Integer.parseInt(hashMap.get("number").toString());
			}
		}
		result.put("all_car", online_car + offline_car);
		result.put("online_car", online_car);
		result.put("offline_car", offline_car);// 离线车辆数量
		return result;
	
    }
    @Override
    public Map<String, Object> getIndexMonitorTaskCount(Map<String,Object> map){
        List<IndexMonitorCar> indexMonitorTasks = indexMonitorDao.getIndexMonitorTaskCount(map);
        Map<String, Object> result = new HashMap<String, Object>();
        List<String> list = new ArrayList<>();
        list.add("5");
        list.add("6");
        //app端点击结束以后，web首页待执行数减 - 结束的任务
        int overCount = 0;
        result.put("list", list);
        overCount = controlDao.countBy(result);
        result.put("all_task",indexMonitorTasks.size());
        int countWaitAllow = 0;
        int countWaitApply = 0;
        int countWaitDo = 0;
        int countRevoke = 0;
        
        
        for(int i = 0; i < indexMonitorTasks.size(); i++){
            IndexMonitorCar indexMonitorCar = indexMonitorTasks.get(i);
            if(indexMonitorCar.getSp_status() == 1){
                countWaitAllow++;
            }else if(indexMonitorCar.getSp_status() == 2 && indexMonitorCar.getDd_status() == 1){
                countWaitApply++;
            }else if(indexMonitorCar.getSp_status() == 3 || indexMonitorCar.getDd_status() == 3){
            	countRevoke++;
            }
        }
        countWaitDo = indexMonitorTasks.size() - countWaitAllow - countWaitApply - countRevoke - overCount;
        result.put("wait_allow",countWaitAllow);
        result.put("wait_apply",countWaitApply);
        result.put("wait_do",countWaitDo);
        return  result;
    }

    @Override
    public void addMonitorCar(IndexMonitorCar indexMonitorCar) {
        indexMonitorDao.addMonitorCar(indexMonitorCar);
    }

    @Override
    public void removeMonitorCarById(IndexMonitorCar indexMonitorCar) {
        indexMonitorDao.removeMonitorCarById(indexMonitorCar);
    }
}
