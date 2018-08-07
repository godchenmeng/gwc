package com.youxing.car.service.base.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.service.base.BaseService;

@SuppressWarnings("all")
@Transactional
public abstract class BaseServiceImpl<T> implements BaseService<T>{
	
	public abstract BaseDao<T> getBaseDao();
	
	// - 增改
	public void add(T entity) {
		this.getBaseDao().add(entity);
	}

	public void modify(T entity) {
		this.getBaseDao().modify(entity);
	}

	// - 删除
	public void removeById(Serializable id) { 
		this.getBaseDao().removeById(id);
	}

	public void removeByIds(List ids) {
		this.getBaseDao().removeByIds(ids);
	}
	
	// - 批量查询
	public List<T> listAll() {
		return this.getBaseDao().listAll();
	}
	public List<T> pageBy(Map map){
		return this.getBaseDao().pageBy(map);
	}
	public List<T> listBy(Map map){
		return this.getBaseDao().listBy(map);
		
	}
	
	// - 单项查询
	public T findById(Serializable id){
		return this.getBaseDao().findById(id);
	}
	public T findByMap(Map map){
		return this.getBaseDao().findByMap(map);
		
	}
	public T findBy(T entity){
		return this.getBaseDao().findBy(entity);
		
	}
	
	// - 统计
	public int countAll(){
		return this.getBaseDao().countAll();
	}
	public int countBy(Map<?, ?> map) {
		return this.getBaseDao().countBy(map);
	}
	
	// - 分页
	public List<T> pageAll(int  start,int limit){
		Map<String, Object> pagerMap = new HashMap<String, Object>();
		pagerMap.put("startIdx", start);
		pagerMap.put("limit", limit);
		List<T> items = this.getBaseDao().pageBy(pagerMap);
	//	pager.setRows(items);
		return items;
	}
}
