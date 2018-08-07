package com.youxing.car.generators;

public class GenerateOrder {

	private static long oldOrder = 0L;
	private static final String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private GenerateOrder() {

	}

	public static synchronized String newOrder() {
		long l;

		for (l = System.currentTimeMillis(); l <= oldOrder; l++)
			;
		oldOrder = l;
		StringBuffer stringbuffer = new StringBuffer(10);
		for (; l > 0L; l /= 36L)
			stringbuffer.insert(0, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt((int) (l % 36L)));

		return stringbuffer.toString();
	}

}