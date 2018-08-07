package com.youxing.car.response;
public abstract interface Response
{
  public static final String CODE_FAILED = "0";
  public static final String MSG_FAILED = "failed";
  public static final String CODE_SUCCESS = "1";
  public static final String MSG_SUCCESS = "success";

  public abstract String getResponseCode();

  public abstract String getResponseMsg();
}

