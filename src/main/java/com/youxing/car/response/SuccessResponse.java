package com.youxing.car.response;


public class SuccessResponse implements Response{
  public String getResponseCode()
  {
    return "1";
  }

  public String getResponseMsg()
  {
    return "success";
  }
}