package com.youxing.car.service.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.youxing.car.dao.InfoCenterDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Alm;
import com.youxing.car.entity.CarAlmType;
import com.youxing.car.entity.CountInfo;
import com.youxing.car.entity.IllegalInfo;
import com.youxing.car.entity.InfoCenter;
import com.youxing.car.service.InfoCenterService;
import com.youxing.car.service.base.impl.BaseServiceImpl;
@Service
public class InfoCenterServiceImpl extends BaseServiceImpl<InfoCenter> implements InfoCenterService{
	@Resource
	private InfoCenterDao infocenterDao;	
	public BaseDao<InfoCenter> getBaseDao() {
		return this.infocenterDao;
	}
	@Override
	public List<CarAlmType> countByGroup(Map<String, Object> map) {
		return infocenterDao.countByGroup(map);
	}
	@Override
	public List<Alm> countAlmByDay(Map<String,Object> map) {
		return infocenterDao.countAlmByDay(map);
	}
	@Override
	public int countCarAlm(Map<String, Object> map) {
		return infocenterDao.countCarAlm(map);
	}
	@Override
	public List<InfoCenter> findInfoListService(Map<String,Object> map) {
		return infocenterDao.findInfoList(map);
	}
	
	
	@Override
	public List<InfoCenter> findIllegalInfoService(Map map) {
		return infocenterDao.findIllegalInfo(map);
	}



	@Override
	public List<InfoCenter> exceptionCountService(Map map) {
		return infocenterDao.exceptionCount(map);
	}


	@Override
	public int countByService(Map map) {
		
		return infocenterDao.countBy(map);
	}


	@Override
	public List<InfoCenter> exceptionAllCountService(Map maps) {
		return infocenterDao.exceptionAllCount(maps);
	}




	@Override
	public List<InfoCenter> dayExCountService(Map map) {
		return infocenterDao.dayExCount(map);
	}


	@Override
	public int updateStatusService(String id) {
		return infocenterDao.updateStatus(id);
	}


	@Override
	public List<InfoCenter> exportExcelService(Map map) {
		return infocenterDao.exportExcel(map);
	}


	@Override
	public List<InfoCenter> exceptionExcelService(Map map) {
		return infocenterDao.exceptionExcel(map);
	}



	@Override
	public int exCountService(Map map) {
		
		return infocenterDao.exCount(map);
	}
	@Override
	public int findInfoCount(Map map) {
		// TODO Auto-generated method stub
		return infocenterDao.findInfoCount(map);
	}
	@Override
	public List<InfoCenter> getInfoByCar(Map map){
		return  infocenterDao.getInfoByCar(map);
	}
	@Override
	public 	List<InfoCenter> overSpeedCountService(Map map) {
		return infocenterDao.overSpeedCount(map);
	}
	@Override
	public List<HashMap<String, Object>> overSpeedCountByCarService(Map<String, Object> map) {
		return infocenterDao.overSpeedCountByCar(map);
	}
	@Override
	public List<InfoCenter> overSpeedCountByOrgExService(Map<String, Object> map) {
		return infocenterDao.overSpeedCountByOrgEx(map);
	}
	@Override
	public List<CountInfo> overSpeedCountByCarExService(Map<String, Object> map) {
		return infocenterDao.overSpeedCountByCarEx(map);
	}
	@Override
	public List<InfoCenter> overBorderCountByOrgService(Map<String, Object> map) {
		return infocenterDao.overBorderCountByOrg(map);
	}
	@Override
	public int overBorderNumService(Map<String,Object> map) {
		return infocenterDao.overBorderNum(map);
	}
	@Override
	public List<IllegalInfo> overBorderByCarService(Map<String, Object> map) {
	return infocenterDao.overBorderByCar(map);
	}
	@Override
	public List<InfoCenter> overBorderMonthAndWeekService(
			Map<String, Object> map) {
		return infocenterDao.overBorderMonthAndWeek(map);
	}
	@Override
	public List<IllegalInfo> overBorderByCarExService(Map<String, Object> map) {
		return infocenterDao.overBorderByCarEx(map);
	}
	@Override
	public List<InfoCenter> illegalCountByOrgService(Map<String, Object> map) {
		return infocenterDao.illegalCountByOrg(map);
	}
	@Override
	public List<CountInfo> illegalCountByCarService(Map<String, Object> map) {
		return infocenterDao.illegalCountByCar(map);
	}
	@Override
	public int illegalNumService(Map<String, Object> map) {
		return infocenterDao.illegalNum(map);
	}
	@Override
	public List<InfoCenter> illegalCountMonthAndWeekService(
			Map<String, Object> map) {
		return infocenterDao.illegalCountMonthAndWeek(map);
	}
	@Override
	public List<CountInfo> illegalExByCarService(Map<String, Object> map) {
		return infocenterDao.illegalExByCar(map);
	}
	@Override
	public List<InfoCenter> foulTimeByOrgService(Map<String, Object> map) {
		return infocenterDao.foulTimeByOrg(map);
	}
	@Override
	public List<CountInfo> foulTimeByCarService(Map<String, Object> map) {
		return infocenterDao.foulTimeByCar(map);
	}
	@Override
	public int foulTimeNumService(Map<String, Object> map) {
		return infocenterDao.foulTimeNum(map);
	}
	@Override
	public List<InfoCenter> foulTimeCountMonthAndWeekService(
			Map<String, Object> map) {
		return infocenterDao.foulTimeCountMonthAndWeek(map);
	}
	@Override
	public List<InfoCenter> foulTimeExByOrgService(Map<String,Object> map){
		return infocenterDao.foulTimeExByOrg(map);
	}
	@Override
	public List<CountInfo> foulTimeExByCarService(Map<String, Object> map) {
		return infocenterDao.foulTimeExByCar(map);
	}
	@Override
	public List<InfoCenter> driveCountByOrgService(Map<String, Object> map) {
		return infocenterDao.driveCountByOrg(map);
	}
	@Override
	public List<CountInfo> driveCountByCarService(Map<String, Object> map) {
		return infocenterDao.driveCountByCar(map);
	}
	@Override
	public int driverNumService(Map<String, Object> map) {
		return infocenterDao.driverNum(map);
	}
	@Override
	public List<InfoCenter> driveCountMonthAndWeekService(
			Map<String, Object> map) {
		return infocenterDao.driveCountMonthAndWeek(map);
	}
	@Override
	public List<CountInfo> driveExByCarService(Map<String, Object> map) {
		return infocenterDao.driveExByCar(map);
	}
	@Override
	public List<CountInfo> driveThreeService(Map<String, Object> map) {
		return infocenterDao.driveThree(map);
	}
	@Override
	public List<InfoCenter> nonTaskByOrgService(Map<String, Object> map) {
		return infocenterDao.nonTaskByOrg(map);
	}
	@Override
	public List<InfoCenter> nonTaskMonthAndWeekService(Map<String, Object> map) {
		return infocenterDao.nonTaskMonthAndWeek(map);
	}
	@Override
	public List<CountInfo> allCountByTime(Map<String, Object> map) {
		return infocenterDao.allCountByTime(map);
	}
	@Override
	public List<CountInfo> allCountByOrg(Map<String, Object> map) {
		return infocenterDao.allCountByOrg(map);
	}
	@Override
	public List<CountInfo> allCountByCar(Map<String, Object> map) {
		return infocenterDao.allCountByCar(map);
	}
	@Override
	public int allCountByCarNum(Map<String, Object> map) {
		return infocenterDao.allCountByCarNum(map);
	}
	@Override
	public int allCountByTimeNum(Map<String, Object> map) {
		return infocenterDao.allCountByTimeNum(map);
	}
	@Override
	public int allCountByOrgNum(Map<String, Object> map) {
		return infocenterDao.allCountByOrgNum(map);
	}
	@Override
	public List<CountInfo> allCountByTimeAndMW(Map<String, Object> map) {
		return infocenterDao.allCountByTimeAndMW(map);
	}
	@Override
	public List<CountInfo> allCountByOrgAndMW(Map<String, Object> map) {
		return infocenterDao.allCountByOrgAndMW(map);
	}
	@Override
	public List<CountInfo> allCountByCarAndMW(Map<String, Object> map) {
		return infocenterDao.allCountByCarAndMW(map);
	}
	@Override
	public int overSpeedNum(Map<String, Object> map) {
		return infocenterDao.overSpeedNum(map);
	}
	@Override
	public List<InfoCenter> infoPage(Map<String, Object> map) {
		return infocenterDao.infoPage(map);
	}
	@Override
	public int infoPageCount(Map<String, Object> map) {
		return infocenterDao.infoPageCount(map);
	}
	@Override
	public void updateInfoStatus(Map<String, Object> map) {
		infocenterDao.updateInfoStatus(map);
	}
}
