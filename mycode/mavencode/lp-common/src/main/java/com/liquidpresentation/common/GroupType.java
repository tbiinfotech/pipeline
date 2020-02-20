package com.liquidpresentation.common;

public enum GroupType {
	sales {
		@Override
		public String toString() {
			return "Sales Group";
		}
	},
	supplier {
		@Override
		public String toString() {
			return "Supplier Group";
		}
	},
	all {
		@Override
		public String toString() {
			return "All Group";
		}
	}
}
