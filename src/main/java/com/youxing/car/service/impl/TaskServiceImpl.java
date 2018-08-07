package com.youxing.car.service.impl;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.youxing.car.dao.TaskDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Task;
import com.youxing.car.service.TaskService;
import com.youxing.car.service.base.impl.BaseServiceImpl;
@Service
public class TaskServiceImpl extends BaseServiceImpl<Task> implements TaskService{
	@Resource
	private TaskDao taskDao;	
	public BaseDao<Task> getBaseDao() {
		return this.taskDao;
	}
}
