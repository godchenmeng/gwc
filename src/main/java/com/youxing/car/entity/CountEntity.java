package com.youxing.car.entity;

/**
 * @author mars
 * @date 2017年5月24日 上午10:29:13
 */
public class CountEntity {

	private String day;
	private String name;
	private Float mil;
	private Float fuel;
	private Integer acce;
	private Integer dece;
	private Integer sharp;
    private Long org;
	
	
	public CountEntity(){
		
	}
	
	public CountEntity(String name, Float mil, Float fuel, Integer acce, Integer dece, Integer sharp,Long org) {
		super();
		this.name = name;
		this.mil = mil;
		this.fuel = fuel;
		this.acce = acce;
		this.dece = dece;
		this.sharp = sharp;
		this.org = org;
	}

	public CountEntity(String name,Long org){
		this.acce = 0;
		this.dece = 0;
		this.sharp = 0;
		this.fuel = 0.0f;
		this.mil = 0.0f;
		this.name = name;
		this.org = org;
	}
	
	public Long getOrg() {
		return org;
	}

	public void setOrg(Long org) {
		this.org = org;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public Float getMil() {
		return this.mil;
	}

	public void setMil(Float mil) {
			this.mil = mil;
	}

	public Float getFuel() {
		return this.fuel;
	}

	public void setFuel(Float fuel) {
		this.fuel = fuel;
	}

	public Integer getAcce() {
		return acce;
	}

	public void setAcce(Integer acce) {
		this.acce = acce;
	}

	public Integer getDece() {
		return dece;
	}

	public void setDece(Integer dece) {
		this.dece = dece;
	}

	public Integer getSharp() {
		return sharp;
	}

	public void setSharp(Integer sharp) {
		this.sharp = sharp;
	}
}
