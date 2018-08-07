package com.youxing.car.utils.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Page<T> implements Serializable {
	  private int draw;
	  private int recordsTotal;
	  private int recordsFiltered;
	  private List<T> data = new ArrayList();
	  private String error;

	  public Page()
	  {
	  }

	  public Page(int draw, List<T> data,int recordsFiltered)
	  {
	    this.draw=draw;
	    this.data=data;
	    this.recordsTotal=data.size();
	    this.recordsFiltered=recordsFiltered;
	  }

	  public int getDraw()
	  {
	    return this.draw;
	  }

	  public void setDraw(int draw)
	  {
	    this.draw = draw;
	  }

	  public int getRecordsTotal()
	  {
	    return this.recordsTotal;
	  }

	  public void setRecordsTotal(int recordsTotal)
	  {
	    this.recordsTotal = recordsTotal;
	  }

	  public int getRecordsFiltered()
	  {
	    return this.recordsFiltered;
	  }

	  public void setRecordsFiltered(int recordsFiltered)
	  {
	    this.recordsFiltered = recordsFiltered;
	  }

	  public List<T> getData()
	  {
	    return this.data;
	  }

	  public void setData(List<T> data)
	  {
	    this.data = data;
	  }

	  public String getError()
	  {
	    return this.error;
	  }

	  public void setError(String error)
	  {
	    this.error = error;
	  }
}
