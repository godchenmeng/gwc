package com.youxing.car.utils.page;


public class DTPager {
	private int draw;// 当前页
	private int start;// 起始
	private int length;// 长度	

	public int getDraw() {
		return this.draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	public int getStart() {
		return this.start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLength() {
		return this.length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String toString() {
		return "DTPager [draw=" + this.draw + ", start=" + this.start + ", length=" + this.length + "]";
	}
}
