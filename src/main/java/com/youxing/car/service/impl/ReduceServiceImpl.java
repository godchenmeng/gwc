package com.youxing.car.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.youxing.car.util.Constant;
import org.springframework.stereotype.Service;

import com.youxing.car.dao.BehaviorDao;
import com.youxing.car.dao.CarDao;
import com.youxing.car.dao.DeviceDao;
import com.youxing.car.entity.Car;
import com.youxing.car.entity.CountEntity;
import com.youxing.car.entity.Device;
import com.youxing.car.service.ReduceService;
import com.youxing.car.util.DateUtils;

/**
 * @author mars
 * @date 2017年5月24日 下午1:47:14
 */
@Service
public class ReduceServiceImpl implements ReduceService {
	@Resource
	private CarDao carDao;
	@Resource
	private DeviceDao deviceDao;
	@Resource
	private BehaviorDao behaviorDao;

	@Override
	public void reducebyId(List<String> device, Long start, Long end, List<CountEntity> ceList, String name, Long org) {
		//Gson gson = new Gson();
		Float mileage = 0f, fuel = 0f;
		int acce = 0, dcce = 0, sharp = 0;
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("devices",device);
		map.put("start", DateUtils.getLong2Date(start));
		map.put("end", DateUtils.getLong2Date(end));
		List<CountEntity> tmpList = behaviorDao.findListForReduceDayWeb(map);
		if(tmpList != null && tmpList.size() > 0){
			for(CountEntity ce:tmpList){
				mileage = mileage + ce.getMil();
				fuel = fuel + ce.getFuel();
				acce = acce + ce.getAcce();
				dcce = dcce + ce.getDece();
				sharp = sharp + ce.getSharp();				
			}
		}
		if (mileage > 0) {
			BigDecimal b = new BigDecimal(mileage / 1000.0);
			mileage = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
		}
		if (fuel > 0) {
			BigDecimal b = new BigDecimal(fuel / 1000.0);
			fuel = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
		}
		CountEntity ce = new CountEntity(name, mileage, fuel, acce, dcce, sharp, org);
		ceList.add(ce);

	}

	public void reduceDetail(List<String> device, String st, Long start, Long end, List<CountEntity> ceList, String name) throws ParseException {
		//Gson gson = new Gson();
		Float mileage = 0f, fuel = 0f;
		int acce = 0, dcce = 0, sharp = 0;
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("devices",device);
		map.put("start", DateUtils.getLong2Date(start));
		map.put("end", DateUtils.getLong2Date(end));
		List<CountEntity> tmpList = behaviorDao.findListForReduceDayWeb(map);
		if(tmpList != null && tmpList.size() > 0){
			for(CountEntity ce:tmpList){
				ce.setName(name);
				mileage = mileage + ce.getMil();
				fuel = fuel + ce.getFuel();
				acce = acce + ce.getAcce();
				dcce = dcce + ce.getDece();
				sharp = sharp + ce.getSharp();
				Float mileages = ce.getMil();
				Float fuels = ce.getFuel();
				if (mileages > 0) {
					BigDecimal b = new BigDecimal(mileages / 1000.0);
					mileages = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
					ce.setMil(mileages);
				}
				if (fuels > 0) {
					BigDecimal b = new BigDecimal(fuels / 1000.0);
					fuels = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
					ce.setFuel(fuels);
				}
			}
		}
		CountEntity all = new CountEntity();
		all.setDay("总计");
		all.setAcce(acce);
		all.setDece(dcce);
		all.setSharp(sharp);
		if (mileage > 0) {
			BigDecimal b = new BigDecimal(mileage / 1000.0);
			mileage = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
			all.setMil(mileage);
		}
		if (fuel > 0) {
			BigDecimal b = new BigDecimal(fuel / 1000.0);
			fuel = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
			all.setFuel(fuel);
		}
		ceList.add(all);
	}

	@Override
	public void reduceAll(List<String> device, String st, Long start, Long end, List<CountEntity> ceList, String name, Long org, boolean export) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("devices",device);
		map.put("start", DateUtils.getLong2Date(start));
		map.put("end", DateUtils.getLong2Date(end));
		List<CountEntity> tmpList = behaviorDao.findListForReduceMonthWeb(map);
		if(tmpList != null && tmpList.size() > 0){
			if (export) {
				for(CountEntity ce : tmpList){
					ce.setName(name);
					ce.setOrg(org);
					ce.setDay(Double.valueOf(ce.getDay().substring(5)).intValue() + "月");//数据库返回时间类型为"2017-04"
					Float mileages = ce.getMil();
					Float fuels = ce.getFuel();
					if (mileages > 0) {
						BigDecimal b = new BigDecimal(mileages / 1000.0);
						mileages = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
						ce.setMil(mileages);
					}
					if (fuels > 0) {
						BigDecimal b = new BigDecimal(fuels / 1000.0);
						fuels = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
						ce.setFuel(fuels);
					}
					ceList.add(ce);
				}
			} else {
				Float mileage = 0f, fuel = 0f;
				int acce = 0, dcce = 0, sharp = 0;
				for(CountEntity ce : tmpList){
					mileage = mileage + ce.getMil();
					fuel = fuel + ce.getFuel();
					acce = acce + ce.getAcce();
					dcce = dcce + ce.getDece();
					sharp = sharp + ce.getSharp();					
				}	
				CountEntity all = new CountEntity();
				all.setOrg(org);
				all.setName(name);
				all.setAcce(acce);
				all.setDece(dcce);
				all.setSharp(sharp);
				if (mileage > 0) {
					BigDecimal b = new BigDecimal(mileage / 1000.0);
					mileage = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
				}
				all.setMil(mileage);
				if (fuel > 0) {
					BigDecimal b = new BigDecimal(fuel / 1000.0);
					fuel = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
				}
				all.setFuel(fuel);
				ceList.add(all);
			}
		}
	}

	public int reduceCar(List<String> device, Long start, Long end, int pageIndex, int limit, List<CountEntity> ceList) {
		if(ceList == null) {
			ceList = new ArrayList<CountEntity>();
		}
		
		int totalCount = 0;
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("devices",device);
		map.put("start", DateUtils.getLong2Date(start));
		map.put("end", DateUtils.getLong2Date(end));
		List<CountEntity> tmpList = behaviorDao.findListForReduceDeviceWeb(map);
		totalCount = tmpList.size();
		
		map.put("limit", limit);
		map.put("startIdx", pageIndex*limit);
		tmpList = behaviorDao.findListForReduceDeviceWeb(map);
		
		if(tmpList != null && tmpList.size() > 0){
			for(CountEntity ce:tmpList){
				map.put("status", Constant.ADD_STATUS);
				String id = ce.getDay();
				device.remove(id);
				map.put("device", ce.getDay());
				Device de = deviceDao.findByMap(map);
				if (de != null) {
					Car ca = carDao.findById(de.getCar_id());
					ce.setName(ca.getCar_no());
					ce.setOrg(ca.getId());
				}
				map.clear();
				Float mileages = ce.getMil();
				Float fuels = ce.getFuel();
				if (mileages > 0) {
					BigDecimal b = new BigDecimal(mileages / 1000.0);
					mileages = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					ce.setMil(mileages);
				}
				if (fuels > 0) {
					BigDecimal b = new BigDecimal(fuels / 1000.0);
					fuels = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					ce.setFuel(fuels);
				}			
				ceList.add(ce);
			}
		}
		
		return totalCount;
	}

	public void reduceCar(String device, String st, Long start, Long end, List<CountEntity> ceList, boolean common) throws Exception {
		Float mileage = 0f, fuel = 0f;
		int acce = 0, dcce = 0, sharp = 0;
		//String str = "";
		Car ca = null;
		CountEntity all = new CountEntity();
		Map<String,Object> map = new HashMap<String,Object>();
		//map.put("device",device);
		List<String> devices = new ArrayList<String>();
		devices.add(device);
		map.put("devices", devices);
		map.put("start", DateUtils.getLong2Date(start));
		map.put("end", DateUtils.getLong2Date(end));
		List<CountEntity> tmpList = null;
		if (common) {
			tmpList = behaviorDao.findListForReduceDayWeb(map);
		} else {
			tmpList = behaviorDao.findListForReduceMonthWeb(map);
		}

		map.clear();
		map.put("status", Constant.ADD_STATUS);
		map.put("device", device);
		Device de = deviceDao.findByMap(map);
		if (de != null) {
			ca = carDao.findById(de.getCar_id());
		}
		if(tmpList != null && tmpList.size() >0){
			for(CountEntity ce: tmpList){
				if (ca != null) {
					ce.setName(ca.getCar_no());
					ce.setOrg(ca.getId());
				}
				mileage = mileage + ce.getMil();
				fuel = fuel + ce.getFuel();
				acce = acce + ce.getAcce();
				dcce = dcce + ce.getDece();
				sharp = sharp + ce.getSharp();
				if (!common) {
					ce.setDay(Double.valueOf(ce.getDay().substring(5)).intValue() + "月");
				}
				Float mileages = ce.getMil();
				Float fuels = ce.getFuel();
				if (mileages > 0) {
					BigDecimal b = new BigDecimal(mileages / 1000.0);
					mileages = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					ce.setMil(mileages);
				}
				if (fuels > 0) {
					BigDecimal b = new BigDecimal(fuels / 1000.0);
					fuels = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
					ce.setFuel(fuels);
				}				
			}
		}
		all.setName(ca.getCar_no());
		all.setDay("总计");
		all.setAcce(acce);
		all.setDece(dcce);
		all.setSharp(sharp);
		BigDecimal b = new BigDecimal(fuel / 1000);
		all.setFuel(b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
		b = new BigDecimal(mileage / 1000);
		all.setMil(b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
		ceList.add(all);
	}
}
