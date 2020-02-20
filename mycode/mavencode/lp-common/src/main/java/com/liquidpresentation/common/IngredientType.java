package com.liquidpresentation.common;

public enum IngredientType {
	general {
		@Override
		public String toString() {
			return "general";
		}
	},
	custom {
		@Override
		public String toString() {
			return "custom";
		}
	},
	housemade {
		@Override
		public String toString() {
			return "housemade";
		}
	}
}
