package com.youxing.car.service;

import java.text.ParseException;
import java.util.List;

import com.youxing.car.entity.CountEntity;

/**   
 * @author mars   
 * @date 2017年5月24日 下午1:46:05 
 */
public interface ReduceService {

	//机构统计天
	void reducebyId(List<String> device,Long start,Long end,List<CountEntity> ceList,String name,Long org);
	//导出机构统计每一天的详细
	void reduceDetail(List<String> device, String st,Long start, Long end, List<CountEntity> ceList, String name) throws ParseException;
	//统计每个月的
	void reduceAll(List<String> device, String st,Long start, Long end, List<CountEntity> ceList, String name,Long org,boolean export);
	//统计每个车的四急油耗
	int reduceCar(List<String> device, Long start, Long end, int pageIndex, int limit, List<CountEntity> ceList);
	//导出详情
	void reduceCar(String device,String st, Long start, Long end, List<CountEntity> ceList, boolean common) throws Exception;
}


