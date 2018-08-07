package com.youxing.car.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.youxing.car.entity.Overstep;
import com.youxing.car.service.base.BaseService;

/**
* @author jianlu
* @version 创建时间：2018年1月11日 下午4:15:46
* @ClassName 类名称
* @Description 类描述
*/
public interface OverstepService extends BaseService<Overstep>{
	
	List<Overstep> pageByWeb(Map<?, ?> map);
	
	Integer countByWeb(Map<?, ?> map);
	
	List<Overstep> getInfoDetail(Map<?, ?> map);
	
	//按月查询 (辆) 
	List<HashMap<String,Object>> pageByMonthL(Map<?, ?> map);

	//按月查询 (次)
	List<HashMap<String,Object>> pageByMonthC(Map<?, ?> map);
	
	//按天查询 (辆) 
	List<HashMap<String,Object>> pageByDayL(Map<?, ?> map);

	//按天查询 (次)
	List<HashMap<String,Object>> pageByDayC(Map<?, ?> map);
	
	//根据机构ID查询超速车辆
	List<HashMap<String,Object>> getAllcountByOrgId(Map<?, ?> map);
	
	//根据机构ID查询超速车辆总数
	Integer getAllcount(Map<?, ?> map);
	
	//根据机构ID查询超速大小车辆数
	List<HashMap<String,Object>> getCountByOrgId(Map<?, ?> map);

	//根据车辆查询越界记录
	List<HashMap<String,Object>> overStepByCar(Map<?, ?> map);
	
	//根据车辆查询越界记录数量
	Integer overStepNum(Map<?, ?> map);
	
	//按机构查询越界记录
	List<Map<String,Object>> overStepCountByOrg(Map<String,Object> map);
	
	//导出和按月按周查询共用
	List<Map<String,Object>> overStepMonthAndWeek(Map<String,Object> map);
	
	//越界统计数据导出  按车辆
	List<Map<String,Object>> overStepByCarEx(Map<String,Object> map);
}
