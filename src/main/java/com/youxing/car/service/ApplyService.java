package com.youxing.car.service;
import java.util.List;
import java.util.Map;

import com.youxing.car.entity.Apply;
import com.youxing.car.service.base.BaseService;
public interface ApplyService extends BaseService<Apply>{

	void addControl(Long id, String[] car, String[] driver, Long uid);

	public List<Apply> pagebysubmited(Map<?, ?> map);
	public List<Apply> pagebyreturn(Map<?, ?> map);

	void add(Apply apply, String[] driver, String[] car,Long uid);
	
	/**
	 * web
	 */
	void addControlWeb(String id, String[] car, String[] driver,Long cb);
	void addEmControl(Apply apply, Long[] car, Long[] driver,Long cb);
	void addControlbyApply(Long aid, Long[] car, Long[] driver,Long cb);
	
	public List<Apply> pageByWebService(Map<?, ?> map);
	
	public int countByWeb(Map<?, ?> map);
	
	public List<Apply> getApplyPageService(Map<?, ?> map);
	
	public List<Apply> getApplyPageByRejectService(Map<?, ?> map);
	
	public int getApplyCountService(Map<?, ?> map);
	
	public int getApplyCountRejectService(Map<?, ?> map);
	

	
	public List<Apply> exportApplyEXcelService(Map<?, ?> map);
	
	public List<Apply> exportApplyByRejectService(Map<?, ?> map);
	
	public List<Apply> getApplySpPageService(Map<?, ?> map);
	public int getSpCountService(Map<?, ?> map);
	public List<Apply> exportSpService(Map<?, ?> map);
	
	public List<Apply> getDdPageService(Map<?, ?> map);
	public List<Apply> exportDdService(Map<?, ?> map);
	
	public List<Apply> getPqPageService(Map<?, ?> map);
	public int getPqCountService(Map<?, ?> map);
	public List<Apply> exportpqService(Map<?, ?> map);
	
	public List<Apply> getNewApplyService(); 
	
	public List<Apply> getDdDataService(Map<?, ?> map);
	public int getDdDataCountService(Map<?, ?> map);
	public List<Apply> exportDdDataService(Map<?, ?> map);
	
	
	public Object getAllNum(Map<?, ?> map); 
	public Object getApplyNewFirst(Map<?, ?> map);
	public int getDdNum(Map<?, ?> map);
	

}
