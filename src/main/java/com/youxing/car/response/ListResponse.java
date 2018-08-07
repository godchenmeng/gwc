package com.youxing.car.response;

import java.util.List;

public class ListResponse<T>  implements Response{
	  private String responseCode;
	  private String responseMsg;
	  private List<T> datas;

	  public ListResponse(String responseCode, String responseMsg, List<T> datas)
	  {
	    this.responseCode = responseCode;
	    this.responseMsg = responseMsg;
	    this.datas = datas;
	  }

	  public ListResponse(List<T> datas) {
	    this.responseCode = "1";
	    this.responseMsg = "success";
	    this.datas = datas;
	  }

	  public String getResponseCode()
	  {
	    return this.responseCode;
	  }

	  public String getResponseMsg()
	  {
	    return this.responseMsg;
	  }

	  public List<T> getDatas()
	  {
	    return this.datas;
	  }

	  public void setDatas(List<T> datas)
	  {
	    this.datas = datas;
	  }

	  public void setResponseCode(String responseCode)
	  {
	    this.responseCode = responseCode;
	  }

	  public void setResponseMsg(String responseMsg)
	  {
	    this.responseMsg = responseMsg;
	  }

	  public String toString()
	  {
	    return "ListResponse [responseCode=" + this.responseCode + ", responseMsg=" + this.responseMsg + ", datas=" + this.datas + "]";
	  }
}
