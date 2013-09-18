package com.kovaciny.primexmodel;

public abstract class Products {
	public static Product makeProduct(String type, double gauge, double width, double length) {
		if (type == Product.SHEETS_TYPE) {
			return new Sheet(gauge, width, length);
		} else if (type == Product.ROLLS_TYPE) {
			return new Roll(gauge, width, 0, Roll.CORE_TYPE_R3);
		} else if (type == Product.ROLLSET_TYPE) {
			return new Rollset(gauge, width, 0, 1, Roll.CORE_TYPE_R3);
		} else if (type == Product.SHEETSET_TYPE) {
			return new Sheetset(gauge, width, length, 1);
		}
		else throw new IllegalArgumentException ("unknown product type");
	}
	public static Product makeProduct(String type, double gauge, double width, double length, int numberOfWebs) {
		if (type.equals(Product.SHEETS_TYPE)) {
			return new Sheet(gauge, width, length);
		} else if (type.equals(Product.ROLLS_TYPE)) {
			return new Roll(gauge, width, 0, Roll.CORE_TYPE_R3);
		} else if (type.equals(Product.ROLLSET_TYPE)) {
			return new Rollset(gauge, width * numberOfWebs, 0, numberOfWebs, Roll.CORE_TYPE_R3);
		} else if (type.equals(Product.SHEETSET_TYPE)) {
			return new Sheetset(gauge, width * numberOfWebs, length, numberOfWebs);
		} else throw new IllegalArgumentException ("unknown product type");
	}
}
