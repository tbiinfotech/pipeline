package com.liquidpresentaion.cocktailservice.constants;

public enum FlavorProfile {
	BITTER {
		@Override
		public String toString() {
			return "Bitter";
		}
	},
	BUBBLY {
		@Override
		public String toString() {
			return "Bubbly";
		}
	},
	CREAMY {
		@Override
		public String toString() {
			return "Creamy";
		}
	},
	FRUITY {
		@Override
		public String toString() {
			return "Fruity/Citrus";
		}
	},
	SALTY {
		@Override
		public String toString() {
			return "Salty/Savory";
		}
	},
	SOUR {
		@Override
		public String toString() {
			return "Sour";
		}
	},
	SPICY {
		@Override
		public String toString() {
			return "Spicy";
		}
	},
	SPIRITFORWARD {
		@Override
		public String toString() {
			return "Spirit Forward";
		}
	},
	SWEET {
		@Override
		public String toString() {
			return "Sweet";
		}
	},
	REFRESHING {
		@Override
		public String toString() {
			return "Refreshing";
		}
	},
	HERB {
		@Override
		public String toString() {
			return "Herb";
		}
	},
	SMOKY {
		@Override
		public String toString() {
			return "Smoky";
		}
	},
	VEGETAL {
		@Override
		public String toString() {
			return "Vegetal";
		}
	},
	NUTTY {
		@Override
		public String toString() {
			return "Nutty";
		}
	};
	
	public static String getFlavorProfile (String str) {
		String result = "";
		switch (str) {
		case "Bitter" : 
			result = "BITTER";
			break;
		case "Bubbly" : 
			result = "BUBBLY";
			break;
		case "Creamy" : 
			result = "CREAMY"; 
			break;
		case "Fruity/Citrus" : 
			result = "FRUITY";
			break;
		case "Salty/Savory" : 
			result = "SALTY";
			break;
		case "Sour" :
			result = "SOUR";
			break;
		case "Spicy" :
			result = "SPICY";
			break;
		case "Spirit Forward" :
			result = "SPIRITFORWARD";
			break;
		case "Sweet" : 
			result = "SWEET";
			break;
		case "Refreshing" : 
			result = "REFRESHING";
			break;
		case "Herb" : 
			result = "HERB";
			break;
		case "Smoky" : 
			result = "SMOKY";
			break;
		case "Vegetal" : 
			result = "VEGETAL";
			break;
		case "Nutty" : 
			result = "NUTTY";
			break;
		default:
			break;
		}
		return result;
	}
}
