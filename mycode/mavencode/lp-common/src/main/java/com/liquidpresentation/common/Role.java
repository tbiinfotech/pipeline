package com.liquidpresentation.common;

public enum Role {
	ADMINISTRATOR {
		@Override
		public String toString() {
			return "Administrator";
		}
	},
	MANAGER {
		@Override
		public String toString() {
			return "Manager";
		}
	},
	MIXOLOGIST {
		@Override
		public String toString() {
			return "Mixologist";
		}
	},
	SALES {
		@Override
		public String toString() {
			return "Sales Representative";
		}
	},
	SALESADMIN {
		@Override
		public String toString() {
			return "Sales Administrator";
		}
	},
	SUPPLIER {
		@Override
		public String toString() {
			return "Supplier";
		}
	}
}
