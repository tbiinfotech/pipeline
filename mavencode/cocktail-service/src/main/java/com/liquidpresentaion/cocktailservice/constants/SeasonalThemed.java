package com.liquidpresentaion.cocktailservice.constants;

public enum SeasonalThemed {
	SPRING {
		@Override
		public String toString() {
			return "Spring";
		}
	},
	SUMMER {
		@Override
		public String toString() {
			return "Summer";
		}
	},
	FALL {
		@Override
		public String toString() {
			return "Fall";
		}
	},
	WINTER {
		@Override
		public String toString() {
			return "Winter";
		}
	},
	HALLOWEEN {
		@Override
		public String toString() {
			return "Halloween";
		}
	},
	EASTER {
		@Override
		public String toString() {
			return "Easter";
		}
	},
	NEWYEAR {
		@Override
		public String toString() {
			return "New Year's";
		}
	},
	STPATRICK {
		@Override
		public String toString() {
			return "St. Patrick's Day";
		}
	},
	CHRISTMAS {
		@Override
		public String toString() {
			return "Christmas";
		}
	},
	THANKSGIVING {
		@Override
		public String toString() {
			return "Thanksgiving";
		}
	},
	JULY4TH {
		@Override
		public String toString() {
			return "4th of July";
		}
	},
	CINCODEMAYO {
		@Override
		public String toString() {
			return "Cinco de Mayo";
		}
	},
	MARDIGRAS {
		@Override
		public String toString() {
			return "Mardi Gras";
		}
	};
	
	public static String getSeasonalThemed (String str) {
		String result = "";
		switch (str) {
		case "Spring": 
			result = "SPRING";
			 break;
		case "Summer": 
				result = "SUMMER";
			 break;
		case "Fall": 
				result = "FALL";
			 break;
		case "Winter": 
				result = "WINTER";
			 break;
		case "Halloween": 
				result = "HALLOWEEN";
			 break;
		case "Easter": 
				result = "EASTER";
			 break;
		case "New Year's": 
				result = "NEWYEAR";
			 break;
		case "St. Patrick's Day": 
				result = "STPATRICK";
			 break;
		case "Christmas": 
				result = "CHRISTMAS";
			 break;
		case "Thanksgiving": 
				result = "THANKSGIVING";
			 break;
		case "4th of July": 
				result = "JULY4TH";
			 break;
		case "Cinco de Mayo": 
				result = "CINCODEMAYO";
			 break;
		case "Mardi Gras": 
				result = "MARDIGRAS";
			 break;
		default:
			break;
		}
		return result;
	}
}
