package com.liquidpresentaion.cocktailservice.constants;

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
	};
	
	public static String getOutletType (String str) {
		String result = "";
		switch (str) {
		case "Pool" : 
			result = "POOL"; 
			break;
		case "Patio" : 
			result = "PATIO"; 
			break;
		case "Restaurant" : 
			result = "RESTAURANT"; 
			break;
		case "Lobby" : 
			result = "LOBBY"; 
			break;
		case "Lounge" : 
			result = "LOUNGE"; 
			break;
		case "Nightclub" : 
			result = "NIGHTCLUB"; 
			break;
		case "Fine Dining" : 
			result = "FINEDINING"; 
			break;
		case "Local/Sports Bar" : 
			result = "LOCALSPROTSBAR"; 
			break;
		case "Gastropub" : 
			result = "GASTROPUB"; 
			break;
		default:
			break;
		}
		return result;
	}
}
