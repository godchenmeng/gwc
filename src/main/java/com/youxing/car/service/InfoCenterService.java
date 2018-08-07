package com.youxing.car.service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.youxing.car.entity.Alm;
import com.youxing.car.entity.CarAlmType;
import com.youxing.car.entity.CountInfo;
import com.youxing.car.entity.IllegalInfo;
import com.youxing.car.entity.InfoCenter;
import com.youxing.car.service.base.BaseService;
public interface InfoCenterService extends BaseService<InfoCenter>{

	List<CarAlmType> countByGroup(Map<String,Object> map);
//gy
	List<Alm> countAlmByDay(Map<String,Object> map);
	
	//gy
	int countCarAlm(Map<String,Object> map);
	
	//gy
	List<InfoCenter> findInfoListService(Map<String,Object> map);
	
	/*
	 * web
	 */
	
	    // 查询违规消息
		List<InfoCenter> findIllegalInfoService(Map map);

		// 异常统计
		List<InfoCenter> exceptionCountService(Map map);
		
		// 总数统计
		int countByService(Map map);
		
		int exCountService(Map map);
		
		// 统计违规类型总次数
		List<InfoCenter> exceptionAllCountService(Map maps);
		
		// 查询一个时间段内每一天的各种违规记录
		List<InfoCenter> dayExCountService(Map map);
		
		// 消息中心 点击未处理违规 ，变更处理时间为当前时间
		int updateStatusService(String id);
		
		// 消息中心导出Excel
		List<InfoCenter> exportExcelService(Map map);
		
		// 异常统计导出Excel
		List<InfoCenter> exceptionExcelService(Map map);
		
		//消息中心异常统计记录
		int findInfoCount(Map map);

		//追踪根据时间段获取车辆异常信息
		List<InfoCenter> getInfoByCar(Map map);
		
		//超速统计
		List<InfoCenter> overSpeedCountService(Map map);
		List<HashMap<String, Object>> overSpeedCountByCarService(Map<String,Object> map);
		List<InfoCenter> overSpeedCountByOrgExService(Map<String,Object> map);
		List<CountInfo> overSpeedCountByCarExService(Map<String,Object> map);
		
		
		//越界统计
		List<InfoCenter> overBorderCountByOrgService(Map<String,Object> map);
		int overBorderNumService(Map<String,Object> map);
		List<IllegalInfo> overBorderByCarService(Map<String,Object> map);
		List<InfoCenter> overBorderMonthAndWeekService(Map<String,Object> map);
		List<IllegalInfo> overBorderByCarExService(Map<String,Object> map);
		
		//违章统计
		List<InfoCenter> illegalCountByOrgService(Map<String,Object> map);
		List<CountInfo> illegalCountByCarService(Map<String,Object> map);
		int illegalNumService(Map<String,Object> map);
		List<InfoCenter> illegalCountMonthAndWeekService(Map<String,Object> map);
		List<CountInfo> illegalExByCarService(Map<String,Object> map);
		
		//非规定时段
		List<InfoCenter> foulTimeByOrgService(Map<String,Object> map);
		List<CountInfo> foulTimeByCarService(Map<String,Object> map);
		int foulTimeNumService(Map<String,Object> map);
		//按机构导出和按周按月查询共用
		List<InfoCenter> foulTimeCountMonthAndWeekService(Map<String,Object> map);
		List<InfoCenter> foulTimeExByOrgService(Map<String,Object> map);
		List<CountInfo> foulTimeExByCarService(Map<String,Object> map);
		
		
		//驾驶数据统计
		List<InfoCenter> driveCountByOrgService(Map<String,Object> map);
		List<CountInfo> driveCountByCarService(Map<String,Object> map);
		int driverNumService(Map<String,Object> map);
		List<InfoCenter> driveCountMonthAndWeekService(Map<String,Object> map);
		List<CountInfo> driveExByCarService(Map<String,Object> map);
		List<CountInfo> driveThreeService(Map<String,Object> map);
		
		//无单用车统计
		List<InfoCenter> nonTaskByOrgService(Map<String,Object> map);
		List<InfoCenter> nonTaskMonthAndWeekService(Map<String,Object> map);
		
		//综合统计
		List<CountInfo> allCountByTime(Map<String,Object> map);
		List<CountInfo> allCountByOrg(Map<String,Object> map);
		List<CountInfo> allCountByCar(Map<String,Object> map);
		
		int allCountByCarNum(Map<String,Object> map);
		int allCountByTimeNum(Map<String,Object> map);
		int allCountByOrgNum(Map<String,Object> map);
		
		List<CountInfo> allCountByTimeAndMW(Map<String,Object> map);
		List<CountInfo> allCountByOrgAndMW(Map<String,Object> map);
		List<CountInfo> allCountByCarAndMW(Map<String,Object> map);
		
		int overSpeedNum(Map<String,Object> map);
		
		
		//消息分页查询
		List<InfoCenter> infoPage(Map<String,Object> map);
		int infoPageCount(Map<String,Object> map);
		//消息状态处理
		void updateInfoStatus(Map<String,Object> map);
		
	
}
