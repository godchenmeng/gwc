package com.youxing.car.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Speeding implements Serializable {
    private Long id;
    private String speeding_name;
    private Integer speeding_limit;
    private int speeding_draw_type;
    private String speeding_draw_path;
    private String speeding_relation_car_ids;
    private int speeding_status;
    private Long speeding_org;
    private Long speeding_createby;
    private Integer speeding_time;
    private String createdate;
    private String updatedate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpeeding_name() {
        return speeding_name;
    }

    public void setSpeeding_name(String speeding_name) {
        this.speeding_name = speeding_name;
    }

    
    public Integer getSpeeding_limit() {
		return speeding_limit;
	}

	public void setSpeeding_limit(Integer speeding_limit) {
		this.speeding_limit = speeding_limit;
	}

	public int getSpeeding_draw_type() {
        return speeding_draw_type;
    }

    public void setSpeeding_draw_type(int speeding_draw_type) {
        this.speeding_draw_type = speeding_draw_type;
    }

    public String getSpeeding_draw_path() {
        return speeding_draw_path;
    }

    public void setSpeeding_draw_path(String speeding_draw_path) {
        this.speeding_draw_path = speeding_draw_path;
    }

    public String getSpeeding_relation_car_ids() {
        return speeding_relation_car_ids;
    }

    public void setSpeeding_relation_car_ids(String speeding_relation_car_ids) {
        this.speeding_relation_car_ids = speeding_relation_car_ids;
    }

    public int getSpeeding_status() {
        return speeding_status;
    }

    public void setSpeeding_status(int speeding_status) {
        this.speeding_status = speeding_status;
    }

    public Long getSpeeding_org() {
        return speeding_org;
    }

    public void setSpeeding_org(Long speeding_org) {
        this.speeding_org = speeding_org;
    }

    public Long getSpeeding_createby() {
        return speeding_createby;
    }

    public void setSpeeding_createby(Long speeding_createby) {
        this.speeding_createby = speeding_createby;
    }

	public String getCreatedate() {
		return createdate;
	}

	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}

	public String getUpdatedate() {
		return updatedate;
	}

	public void setUpdatedate(String updatedate) {
		this.updatedate = updatedate;
	}

	public Integer getSpeeding_time() {
		return speeding_time;
	}

	public void setSpeeding_time(Integer speeding_time) {
		this.speeding_time = speeding_time;
	}
}
