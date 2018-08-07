package com.youxing.car.entity;



public class JsonObject {
	  Object state;
	  Object result;
	  Object message;
      public JsonObject(){  
      }
	  public JsonObject(Object state, Object message) {
	    this.state = state;
	    this.message = message;
	  }

	  public JsonObject(Object state, Object result, Object message) {
	    this.state = state;
	    this.result = result;
	    this.message = message;
	  }

	  public Object getMessage() {
	    return message;
	  }

	  public void setMessage(Object message) {
	    this.message = message;
	  }

	  public Object getState() {
	    return state;
	  }

	  public void setState(Object state) {
	    this.state = state;
	  }

	  public Object getResult() {
	    return result;
	  }

	  public void setResult(Object result) {
	    this.result = result;
	  }

	  @Override
	  public String toString() {
	    return "JsonObject [state=" + state + ", result=" + result + ", message=" + message + "]";
	  }

}
