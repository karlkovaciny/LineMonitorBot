package com.kovaciny.primexmodel;

public abstract class Products {
	public static Product makeProduct(String type, double gauge, double width, double length) {
		if (type == Product.SHEETS_TYPE) {
			return new Sheet(gauge, width, length);
		} else if (type == Product.ROLLS_TYPE) {
			return new Roll(gauge, width, 0);
		} else throw new RuntimeException ("unknown product type");
	}
}
