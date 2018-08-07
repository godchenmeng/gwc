package com.youxing.car.entity;
/**   
 * @author mars   
 * @date 2017年4月20日 上午9:31:13 
 */
public class CarType {

	private String value;
	private String text;

	public CarType(){
		
	}
	public CarType(String value, String text) {
		super();
		this.value = value;
		this.text = text;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}


