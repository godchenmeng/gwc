package com.youxing.car.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * { "rows" : [{},{}], //数据集合 "results" : 100, //记录总数 "hasError" : false,
 * //是否存在错误 "error" : "" // 仅在 hasError : true 时使用
 * 
 * @author Administrator
 *
 * @param <T>
 */
public class Page<T> implements Serializable {
	// 多少页
	private int records;
	// 多少条数据
	private int totalCount;
	private int results;
	private List<T> rows = new ArrayList();
	private String error;

	public Page() {
	}

	public Page(List<T> data, int totalCount, int limit) {
		int totalPage = totalCount / limit;
		if (totalPage == 0 || totalCount % limit != 0) {
			totalPage++;
		}
		this.records = totalPage;
		this.rows = data;
		this.results=totalCount;
		this.totalCount = totalCount;
	}

	
	public int getResults() {
		return results;
	}

	public void setResults(int results) {
		this.results = results;
	}

	public int getRecords() {
		return records;
	}

	public void setRecords(int records) {
		this.records = records;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}

	public String getError() {
		return this.error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
