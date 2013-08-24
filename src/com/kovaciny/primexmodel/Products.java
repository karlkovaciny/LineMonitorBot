package com.kovaciny.primexmodel;

public abstract class Products {
	public static Product makeProduct(String type, double gauge, double width, double length) {
		if (type == Product.SHEETS_TYPE) {
			return new Sheet(gauge, width, length);
		} else if (type == Product.ROLLS_TYPE) {
			return new Roll(gauge, width, 0);
		} else if (type == Product.ROLLSET_TYPE) {
			return new Rollset(gauge, width, 0, 1);
		} else throw new IllegalArgumentException ("unknown product type");
	}
	public static Product makeProduct(String type, double gauge, double width, double length, int numberOfWebs) {
		if (type == Product.SHEETS_TYPE) {
			return new Sheet(gauge, width, length);
		} else if (type == Product.ROLLS_TYPE) {
			return new Roll(gauge, width, 0);
		} else if (type == Product.ROLLSET_TYPE) {
			return new Rollset(gauge, width * numberOfWebs, 0, numberOfWebs);
		} else throw new IllegalArgumentException ("unknown product type");
	}
}
