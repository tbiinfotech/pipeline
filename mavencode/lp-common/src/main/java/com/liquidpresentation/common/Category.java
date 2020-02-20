package com.liquidpresentation.common;

/**
 * This is Ingredient Category
 * @author HP
 *
 */
public enum Category {
	BASESPIRIT {
		@Override
		public String toString() {
			return "Base Spirit/Modifier";
		}
	},
	JUICE {
		@Override
		public String toString() {
			return "Juice/Liquid";
		}
	},
	SWEETENER {
		@Override
		public String toString() {
			return "Sweetener";
		}
	},
	SOLIDS {
		@Override
		public String toString() {
			return "Solids";
		}
	},
	GARNISH {
		@Override
		public String toString() {
			return "Garnish";
		}
	},
	HOUSEMADE {
		@Override
		public String toString() {
			return "Housemade";
		}
	},
	CUSTOM {
		@Override
		public String toString() {
			return "CUSTOM";
		}
	}
}
