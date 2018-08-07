package com.youxing.car.entity;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class Fence implements Serializable {
    private Long id;
    private String fence_name;
    private int fence_type;
    private int fence_draw_type;
    private String fence_draw_path;
    private String fence_relation_car_ids;
    private int fence_status;
    private Long fence_org;
    private Long fence_createby;
    private Date createdate;

    public String getFence_relation_car_ids() {
        return fence_relation_car_ids;
    }

    public void setFence_relation_car_ids(String fence_relation_car_ids) {
        this.fence_relation_car_ids = fence_relation_car_ids;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFence_name() {
        return fence_name;
    }

    public void setFence_name(String fence_name) {
        this.fence_name = fence_name;
    }

    public int getFence_type() {
        return fence_type;
    }

    public void setFence_type(int fence_type) {
        this.fence_type = fence_type;
    }

    public int getFence_draw_type() {
        return fence_draw_type;
    }

    public void setFence_draw_type(int fence_draw_type) {
        this.fence_draw_type = fence_draw_type;
    }

    public String getFence_draw_path() {
        return fence_draw_path;
    }

    public void setFence_draw_path(String fence_draw_path) {
        this.fence_draw_path = fence_draw_path;
    }

    public int getFence_status() {
        return fence_status;
    }

    public void setFence_status(int fence_status) {
        this.fence_status = fence_status;
    }

    public Long getFence_org() {
        return fence_org;
    }

    public void setFence_org(Long fence_org) {
        this.fence_org = fence_org;
    }

    public Long getFence_createby() {
        return fence_createby;
    }

    public void setFence_createby(Long fence_createby) {
        this.fence_createby = fence_createby;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }
}
