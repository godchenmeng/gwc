package com.youxing.car.dao;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Alm;
import com.youxing.car.entity.CarAlmType;
import com.youxing.car.entity.CountInfo;
import com.youxing.car.entity.IllegalInfo;
import com.youxing.car.entity.InfoCenter;
public interface InfoCenterDao extends BaseDao<InfoCenter>{
	
	List<CarAlmType> countByGroup(Map<String,Object> map);
	List<Alm> countAlmByDay(Map<String,Object> map);
	
	int countCarAlm(Map<String,Object> map);
	
	List<InfoCenter> findInfoList(Map<String,Object> map);
	

	/*
	 * web
	 */
	// 查询违规信息
	List<InfoCenter> findIllegalInfo(Map map);

	// 根据违规类型查询违规记录
	List<InfoCenter> findIllegalCountType(String type);

	// 异常统计（表格显示）
	List<InfoCenter> exceptionCount(Map map);
	
	// 统计总数据（用于返回总条数用作分页显示）
	int countBy(Map map);
	
	int exCount(Map map);
	
	// 统计违规类型总次数
	List<InfoCenter> exceptionAllCount(Map maps);
	
	// 查询一个时间段内每一天的各种违规记录
	List<InfoCenter> dayExCount(Map map);
	
	// 消息中心 点击未处理违规 ，变更处理时间为当前时间
	int updateStatus(String id);
	
	// 消息中心导出Excel
	List<InfoCenter> exportExcel(Map map);
	
	// 异常统计导出Excel
	List<InfoCenter>  exceptionExcel(Map map);
	
	//消息中心异常统计记录
	int findInfoCount(Map map);

	//追踪根据时间段获取车辆异常信息
	List<InfoCenter> getInfoByCar(Map map);
	
	//超速统计
	List<InfoCenter> overSpeedCount(Map map);
	List<HashMap<String, Object>> overSpeedCountByCar(Map<String,Object> map);
    List<InfoCenter> overSpeedCountByOrgEx(Map<String,Object> map);
	List<CountInfo> overSpeedCountByCarEx(Map<String,Object> map);
	
	//越界统计
	List<InfoCenter> overBorderCountByOrg(Map<String,Object> map);
	int overBorderNum(Map<String,Object> map);
	List<IllegalInfo> overBorderByCar(Map<String,Object> map);
	//导出和按月按周查询共用
	List<InfoCenter> overBorderMonthAndWeek(Map<String,Object> map);
	List<IllegalInfo> overBorderByCarEx(Map<String,Object> map);
	
	//违章统计
	List<InfoCenter> illegalCountByOrg(Map<String,Object> map);
	List<CountInfo> illegalCountByCar(Map<String,Object> map);
	int illegalNum(Map<String,Object> map);
	//按机构导出和按周按月查询共用
	List<InfoCenter> illegalCountMonthAndWeek(Map<String,Object> map);
	List<CountInfo> illegalExByCar(Map<String,Object> map);
	
	
	//非规定时段
	List<InfoCenter> foulTimeByOrg(Map<String,Object> map);
	List<CountInfo> foulTimeByCar(Map<String,Object> map);
	int foulTimeNum(Map<String,Object> map);
	//按机构导出和按周按月查询共用
	List<InfoCenter> foulTimeCountMonthAndWeek(Map<String,Object> map);
	List<InfoCenter> foulTimeExByOrg(Map<String,Object> map);
	List<CountInfo> foulTimeExByCar(Map<String,Object> map);
	
	
	//驾驶数据统计
	List<InfoCenter> driveCountByOrg(Map<String,Object> map);
	List<CountInfo> driveCountByCar(Map<String,Object> map);
	int driverNum(Map<String,Object> map);
	List<InfoCenter> driveCountMonthAndWeek(Map<String,Object> map);
	List<CountInfo> driveExByCar(Map<String,Object> map);
	//三急数据统计
	List<CountInfo> driveThree(Map<String,Object> map);
	
	
	//无单用车统计
	List<InfoCenter> nonTaskByOrg(Map<String,Object> map);
	List<InfoCenter> nonTaskMonthAndWeek(Map<String,Object> map);
	
	
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
	
	void updateInfoStatus(Map<String,Object> map);
	
	
	
	
	
	
}
