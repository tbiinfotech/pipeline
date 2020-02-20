package com.liquidpresentaion.users.model;

public enum OutletType {
	POOL {
		@Override
		public String toString() {
			return "Pool";
		}
	},
	PATIO {
		@Override
		public String toString() {
			return "Patio";
		}
	},
	RESTAURANT {
		@Override
		public String toString() {
			return "Restaurant";
		}
	},
	LOBBY {
		@Override
		public String toString() {
			return "Lobby";
		}
	},
	LOUNGE {
		@Override
		public String toString() {
			return "Lounge";
		}
	},
	NIGHTCLUB {
		@Override
		public String toString() {
			return "Nightclub";
		}
	},
	FINEDINING {
		@Override
		public String toString() {
			return "Fine Dining";
		}
	},
	LOCALSPROTSBAR {
		@Override
		public String toString() {
			return "Local/Sports Bar";
		}
	},
	GASTROPUB {
		@Override
		public String toString() {
			return "Gastropub";
		}
	}
}
