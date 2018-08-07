package com.youxing.car.entity;

public class IndexMonitorCar {
    private Long id;
    private Long car_id;
    private Long user_id;
    private String car_no;
    private String acc;
    private int sp_status;
    private int dd_status;
    private String device;

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public int getSp_status() {
        return sp_status;
    }

    public void setSp_status(int sp_status) {
        this.sp_status = sp_status;
    }

    public int getDd_status() {
        return dd_status;
    }

    public void setDd_status(int dd_status) {
        this.dd_status = dd_status;
    }

    public String getAcc() {
        return acc;
    }

    public void setAcc(String acc) {
        this.acc = acc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCar_id() {
        return car_id;
    }

    public void setCar_id(Long car_id) {
        this.car_id = car_id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getCar_no() {
        return car_no;
    }

    public void setCar_no(String car_no) {
        this.car_no = car_no;
    }
}
