package com.youxing.car.dao;
import java.util.List;
import java.util.Map;

import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Apply;
public interface ApplyDao extends BaseDao<Apply>{
	public List<Apply> pagebysubmited(Map<?, ?> map);
	public List<Apply> pagebyreturn(Map<?, ?> map);
	
	public List<Apply> pageByWeb(Map<?, ?> map);
	
	public int countByWeb(Map<?, ?> map);
	
	public List<Apply> getApplyPage(Map<?, ?> map);
	
	public int getApplyCount(Map<?, ?> map);
	
	public List<Apply> getApplyPageByReject(Map<?, ?> map); 
	
	public int getApplyCountReject(Map<?, ?> map);
	
	
	
	public List<Apply> exportApplyEXcel(Map<?, ?> map);
	
	public List<Apply> exportApplyByReject(Map<?, ?> map);
	
	public List<Apply> getApplySpPage(Map<?, ?> map);
	//审批，调度共用
	public int getSpCount(Map<?, ?> map);
	public List<Apply> exportSp(Map<?, ?> map);
	
	public List<Apply> getDdPage(Map<?, ?> map);
	public List<Apply> exportDd(Map<?, ?> map);
	
	public List<Apply> getPqPage(Map<?, ?> map);
	public int getPqCount(Map<?, ?> map);
	public List<Apply> exportpq(Map<?, ?> map);
	
	public List<Apply> getNewApply(); 
	
	public List<Apply> getDdData(Map<?, ?> map);
	public int getDdDataCount(Map<?, ?> map);
	public List<Apply> exportDdData(Map<?, ?> map);
	
	
	public Object getAllNum(Map<?, ?> map); 
	public Object getApplyNewFirst(Map<?, ?> map);
	public int getDdNum(Map<?, ?> map);
	
	
	
	
	
	
	


}
