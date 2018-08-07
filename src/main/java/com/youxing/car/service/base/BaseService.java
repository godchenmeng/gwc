package com.youxing.car.service.base;

import java.io.Serializable;
import java.util.List;
import java.util.Map;



@SuppressWarnings("all")
public interface BaseService<T> {

	// - 增改
	public void add(T entity);// 1.增加

	public void modify(T entity);// 2.修改

	// - 删除
	public void removeById(Serializable id);// 3.删除

	public void removeByIds(List<?> ids);// 4.批量删除

	// - 批量查询
	public List<T> listAll();// 5.查全部

	public List<T> listBy(Map<?, ?> map);// 6.根据条件查全部

	public List<T> pageBy(Map<?, ?> map);// 7.根据条件分页查

	// - 单项查询
	public T findByMap(Map<?, ?> map);// 8.根据条件查询
	
	public T findBy(T entity);

	public T findById(Serializable id);// 9.根据Id查询

	// - 统计
	public int countAll();// 10.统计全部

	public int countBy(Map<?, ?> map);// 11.条件统计

}
