package com.youxing.car.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.youxing.car.util.Constant;
import org.springframework.stereotype.Service;

import com.youxing.car.dao.ApplyDao;
import com.youxing.car.dao.CarDao;
import com.youxing.car.dao.ControlDao;
import com.youxing.car.dao.DriverDao;
import com.youxing.car.dao.base.BaseDao;
import com.youxing.car.entity.Apply;
import com.youxing.car.entity.Car;
import com.youxing.car.entity.Control;
import com.youxing.car.entity.Driver;
import com.youxing.car.service.ApplyService;
import com.youxing.car.service.base.impl.BaseServiceImpl;
import com.youxing.car.util.DateUtils;

@Service
public class ApplyServiceImpl extends BaseServiceImpl<Apply> implements ApplyService {
	@Resource
	private ApplyDao applyDao;
	@Resource
	private CarDao carDao;
	@Resource
	private DriverDao driverDao;
	@Resource
	private ControlDao controlDao;
	public BaseDao<Apply> getBaseDao() {
		return this.applyDao;
	}

	
	public void addControl(Long id, String[] car, String[] driver, Long uid) {
		Date cd = new Date();
		String date = DateUtils.getDate("yyyy-MM-dd HH:mm:ss");
		for (int i = 0; i < car.length; i++) {
			Control ct = new Control();
			ct.setApply_id(id);
			Long car_id = Long.parseLong(car[i]);
			ct.setCar_id(car_id);
			Long dirver_id = Long.parseLong(driver[i]);
			ct.setDriver_id(dirver_id);
			ct.setStatus(Constant.ADD_STATUS);
			Control find = controlDao.findBy(ct);
			if (find == null) {
				ct.setCreateby(uid);
				ct.setCreatedate(DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
			}
			controlDao.add(ct);
			// 修改车辆计划次数
			Car ca = carDao.findById(car_id);
			if (ca != null) {
				Car ncar = new Car();
				if (ca.getPlan() != null) {
					ncar.setPlan(ca.getPlan() + 1);
				} else {
					ncar.setPlan(1);
				}
				ncar.setId(car_id);
				carDao.modify(ncar);
			}
			// 修改驾驶员计划次数
			Driver dr = driverDao.findById(dirver_id);
			if (dr != null) {
				Driver ndr = new Driver();
				if (dr.getPlan() != null) {
					ndr.setPlan(dr.getPlan() + 1);
				} else {
					ndr.setPlan(1);
				}
				ndr.setId(dirver_id);
				driverDao.modify(ndr);
			}
		}
		// 调度状态-已调度
		Apply ap = new Apply();
		ap.setId(id);
		ap.setDd_status(Constant.APPLY_2);
		ap.setDd_time(DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
		ap.setDd(uid);
		applyDao.modify(ap);
	}


	@Override
	public List<Apply> pagebysubmited(Map<?, ?> map) {
		return applyDao.pagebysubmited(map);
	}


	@Override
	public List<Apply> pagebyreturn(Map<?, ?> map) {
		return applyDao.pagebyreturn(map);
	}


	@Override
	public void add(Apply apply, String[] driver, String[] car,Long uid) {
		apply.setSp(uid);
		apply.setSp_status(Constant.APPLY_2);
		apply.setSp_time(apply.getCreatedate());
		apply.setDd_time(apply.getCreatedate());
		apply.setDd_status(Constant.APPLY_2);
		apply.setDd(uid);
		
		apply.setType("1");
		applyDao.add(apply);
		Long aid = apply.getId();
		if(aid == null){
			throw new RuntimeException("用车申请为空！");
		}
		addControl(aid, car, driver, uid);
	}
	
	
	
	/**
	 * ===============================web
	 */
	
	@Override
	public void addEmControl(Apply apply, Long[] car, Long[] driver, Long cb) {
		applyDao.add(apply);
		Long aid = apply.getId();
		addControlbyApply(aid,car,driver,cb);
		
	}

	@Override
	public void addControlbyApply(Long aid, Long[] car, Long[] driver,
			Long cb) {
		Date date = new Date();
		for (int i = 0; i < car.length; i++) {
			Control ct = new Control();
			ct.setApply_id(aid);
			Long car_id = car[i];
			ct.setCar_id(car_id);
			Long dirver_id = driver[i];
			ct.setDriver_id(dirver_id);
			ct.setStatus(Constant.ADD_STATUS);
			Control find = controlDao.findBy(ct);
			if (find == null) {
				ct.setCreateby(cb);
				ct.setCreatedate(DateUtils.getDateTime());
				controlDao.add(ct);
				// 修改车辆计划次数
				Car ca = carDao.findById(car_id);
				if (ca != null) {
					Car ncar = new Car();
					if(ca.getPlan()!=null){					
						ncar.setPlan(ca.getPlan()+1);
					}else{
						ncar.setPlan(1);
					}
					ncar.setId(car_id);
					carDao.modify(ncar);
				}
				// 修改驾驶员计划次数
				Driver dr = driverDao.findById(dirver_id);
				if (dr != null) {
					Driver ndr = new Driver();
					if(dr.getPlan()!=null){					
						ndr.setPlan(dr.getPlan()+1);
					}else{
						ndr.setPlan(1);
					}
					ndr.setId(dirver_id);
					driverDao.modify(ndr);
				}
			}
		}
		
	}


	@Override
	public void addControlWeb(String id, String[] car, String[] driver, Long cb) {
		Long apply = Long.parseLong(id);
		Date date = new Date();
		for (int i = 0; i < car.length; i++) {
			Control ct = new Control();
			ct.setApply_id(apply);
			Long car_id = Long.parseLong(car[i]);
			ct.setCar_id(car_id);
			Long dirver_id = Long.parseLong(driver[i]);
			ct.setDriver_id(dirver_id);
			ct.setStatus(Constant.ADD_STATUS);
			Control find = controlDao.findBy(ct);
			if (find == null) {
				ct.setCreateby(cb);
				ct.setCreatedate(DateUtils.getDateTime());
			}
			controlDao.add(ct);
			// 修改车辆计划次数
			Car ca = carDao.findById(car_id);
			if (ca != null) {
				Car ncar = new Car();
				if(ca.getPlan()!=null){					
					ncar.setPlan(ca.getPlan()+1);
				}else{
					ncar.setPlan(1);
				}
				ncar.setId(car_id);
				ncar.setTask_status("2");
				carDao.modify(ncar);
			}
			// 修改驾驶员计划次数
			Driver dr = driverDao.findById(dirver_id);
			if (dr != null) {
				Driver ndr = new Driver();
				if(dr.getPlan()!=null){					
					ndr.setPlan(dr.getPlan()+1);
				}else{
					ndr.setPlan(1);
				}
				ndr.setId(dirver_id);
				ndr.setTask_status("2");
				driverDao.modifyDriver(ndr);
			}
		}
		// 调度状态-已调度
		Apply ap = new Apply();
		ap.setId(apply);
		ap.setDd_status(Constant.APPLY_2);
		ap.setDd_time(DateUtils.getDateTime());
		applyDao.modify(ap);
	}


	@Override
	public List<Apply> pageByWebService(Map<?, ?> map) {
		return applyDao.pageByWeb(map);
	}


	@Override
	public int countByWeb(Map<?, ?> map) {
		return applyDao.countByWeb(map);
	}


	@Override
	public List<Apply> getApplyPageService(Map<?, ?> map) {
		return applyDao.getApplyPage(map);
	}


	@Override
	public List<Apply> getApplyPageByRejectService(Map<?, ?> map) {
		return applyDao.getApplyPageByReject(map);
	}


	@Override
	public int getApplyCountService(Map<?, ?> map) {
		return applyDao.getApplyCount(map);
	}


	@Override
	public int getApplyCountRejectService(Map<?, ?> map) {
		return applyDao.getApplyCountReject(map);
	}



	@Override
	public List<Apply> exportApplyEXcelService(Map<?, ?> map) {
		return applyDao.exportApplyEXcel(map);
	}


	@Override
	public List<Apply> exportApplyByRejectService(Map<?, ?> map) {
		return applyDao.exportApplyByReject(map);
	}


	@Override
	public List<Apply> getApplySpPageService(Map<?, ?> map) {
		return applyDao.getApplySpPage(map);
	}


	@Override
	public int getSpCountService(Map<?, ?> map) {
		return applyDao.getSpCount(map);
	}


	@Override
	public List<Apply> exportSpService(Map<?, ?> map) {
		return applyDao.exportSp(map);
	}


	@Override
	public List<Apply> getDdPageService(Map<?, ?> map) {
		return applyDao.getDdPage(map);
	}


	@Override
	public List<Apply> exportDdService(Map<?, ?> map) {
		return applyDao.exportDd(map);
	}


	@Override
	public List<Apply> getPqPageService(Map<?, ?> map) {
		return applyDao.getPqPage(map);
	}


	@Override
	public int getPqCountService(Map<?, ?> map) {
		return applyDao.getPqCount(map);
	}


	@Override
	public List<Apply> exportpqService(Map<?, ?> map) {
		return applyDao.exportpq(map);
	}


	@Override
	public List<Apply> getNewApplyService() {
		return applyDao.getNewApply();
	}


	@Override
	public List<Apply> getDdDataService(Map<?, ?> map) {
		return applyDao.getDdData(map);
	}


	@Override
	public int getDdDataCountService(Map<?, ?> map) {
		return applyDao.getDdDataCount(map);
	}


	@Override
	public List<Apply> exportDdDataService(Map<?, ?> map) {
		return applyDao.exportDdData(map);
	}


	@Override
	public Object getAllNum(Map<?, ?> map) {
		return applyDao.getAllNum(map);
	}


	@Override
	public Object getApplyNewFirst(Map<?, ?> map) {
		return applyDao.getApplyNewFirst(map);
	}


	@Override
	public int getDdNum(Map<?, ?> map) {
		return applyDao.getDdNum(map);
	}



}
