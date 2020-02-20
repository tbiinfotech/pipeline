package com.liquidpresentaion.cocktailservice.constants;

/**
 * This is Cocktail category
 * @author HP
 *
 */
public enum Category {
	MIXOLOGY {
		@Override
		public String toString() {
			return "Mixology";
		}
	},
	CLASSIC {
		@Override
		public String toString() {
			return "Classic";
		}
	},
	SHOT {
		@Override
		public String toString() {
			return "Shot";
		}
	},
	SHOOTER {
		@Override
		public String toString() {
			return "Shooter";
		}
	},
	TIKI {
		@Override
		public String toString() {
			return "Tiki";
		}
	},
	PUNCH {
		@Override
		public String toString() {
			return "Punch";
		}
	},
	DESSERT {
		@Override
		public String toString() {
			return "Dessert";
		}
	},
	BRUNCH {
		@Override
		public String toString() {
			return "Brunch";
		}
	},
	POOL {
		@Override
		public String toString() {
			return "Pool";
		}
	},
	FRONZEN {
		@Override
		public String toString() {
			return "Frozen/Blended";
		}
	},
	HOT {
		@Override
		public String toString() {
			return "Hot";
		}
	},
	TROPICAL {
		@Override
		public String toString() {
			return "Tropical";
		}
	},
	MORDERNDAY {
		@Override
		public String toString() {
			return "Modern Day Classic";
		}
	},
	SANGRIA {
		@Override
		public String toString() {
			return "Sangria";
		}
	},
	BEERTAILS {
		@Override
		public String toString() {
			return "Beertails";
		}
	},
	HIGHBALLS {
		@Override
		public String toString() {
			return "Highballs";
		}
	},
	LOWBALLS {
		@Override
		public String toString() {
			return "Lowballs";
		}
	},
	MOCKTAILS {
		@Override
		public String toString() {
			return "Mocktails";
		}
	},
	MUDDLED {
		@Override
		public String toString() {
			return "Muddled";
		}
	},
	SHAKENUP {
		@Override
		public String toString() {
			return "Shaken-up";
		}
	},
	SPARKLINGWINE {
		@Override
		public String toString() {
			return "Sparkling Wine Cocktail";
		}
	},
	STIRREDUP {
		@Override
		public String toString() {
			return "Stirred-Up";
		}
	},
	ROCKS {
		@Override
		public String toString() {
			return "Rocks";
		}
	};
	
	public static String getCategory (String str) {
		String result = "";
		switch (str) {
		case "Mixology" : 
			result = "MIXOLOGY"; 
			break;
		case "Classic" : 
			result = "CLASSIC"; 
			break;
		case "Shot" : 
			result = "SHOT"; 
			break;
		case "Shooter" : 
			result = "SHOOTER"; 
			break;
		case "Tiki" : 
			result = "TIKI"; 
			break;
		case "Punch" : 
			result = "PUNCH"; 
			break;
		case "Dessert" : 
			result = "DESSERT"; 
			break;
		case "Brunch" : 
			result = "BRUNCH"; 
			break;
		case "Pool" : 
			result = "POOL"; 
			break;
		case "Frozen/Blended" : 
			result = "FRONZEN"; 
			break;
		case "Hot" : 
			result = "HOT"; 
			break;
		case "Tropical" : 
			result = "TROPICAL"; 
			break;
		case "Modern Day Classic" : 
			result = "MORDERNDAY"; 
			break;
		case "Sangria" : 
			result = "SANGRIA"; 
			break;
		case "Beertails" : 
			result = "BEERTAILS"; 
			break;
		case "Highballs" : 
			result = "HIGHBALLS"; 
			break;
		case "Lowballs" : 
			result = "LOWBALLS"; 
			break;
		case "Mocktails" : 
			result = "MOCKTAILS"; 
			break;
		case "Muddled" : 
			result = "MUDDLED"; 
			break;
		case "Shaken-up" : 
			result = "SHAKENUP"; 
			break;
		case "Sparkling Wine Cocktail" : 
			result = "SPARKLINGWINE"; 
			break;
		case "Stirred-Up" : 
			result = "STIRREDUP"; 
			break;
		case "Rocks" : 
			result = "ROCKS"; 
			break;
		default:
			break;
		}
		return result;
	}
}
