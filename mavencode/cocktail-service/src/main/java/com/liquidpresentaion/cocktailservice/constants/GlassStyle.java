package com.liquidpresentaion.cocktailservice.constants;

public enum GlassStyle {
	MARTINI {
		@Override
		public String toString() {
			return "Martini";
		}
	},
	HIGHBALL {
		@Override
		public String toString() {
			return "Highball";
		}
	},
	SHOT {
		@Override
		public String toString() {
			return "Shot";
		}
	},
	OLDFASHIONED {
		@Override
		public String toString() {
			return "Old Fashioned";
		}
	},
	ROCKS {
		@Override
		public String toString() {
			return "Rocks";
		}
	},
	COCKTAIL {
		@Override
		public String toString() {
			return "Cocktail";
		}
	},
	MARGARITA {
		@Override
		public String toString() {
			return "Margarita";
		}
	},
	BEERGLASS {
		@Override
		public String toString() {
			return "Beer/Glass";
		}
	},
	CHAMPAGNEFLUTE {
		@Override
		public String toString() {
			return "Champagne Flute";
		}
	},
	PITCHER {
		@Override
		public String toString() {
			return "Pitcher";
		}
	},
	PUNCHBOWL {
		@Override
		public String toString() {
			return "Punch Bowl";
		}
	},
	COPPERMUG {
		@Override
		public String toString() {
			return "Copper Mug";
		}
	},
	OTHER {
		@Override
		public String toString() {
			return "Other";
		}
	},
	COLLINS {
		@Override
		public String toString() {
			return "Collins";
		}
	},
	CORDIALPONY {
		@Override
		public String toString() {
			return "Cordial or Pony";
		}
	},
	REDWINE {
		@Override
		public String toString() {
			return "Red Wine";
		}
	},
	WHITEWINE {
		@Override
		public String toString() {
			return "White Wine";
		}
	},
	SHERRY {
		@Override
		public String toString() {
			return "Sherry";
		}
	},
	POUSSECAFE {
		@Override
		public String toString() {
			return "Pousse Café";
		}
	},
	CHAMPANGECOUPE {
		@Override
		public String toString() {
			return "Champange Coupe";
		}
	},
	HURRICANE {
		@Override
		public String toString() {
			return "Hurricane";
		}
	},
	PARFAIT {
		@Override
		public String toString() {
			return "Parfait";
		}
	},
	MOSCOWMULEMUG {
		@Override
		public String toString() {
			return "Moscow Mule Mug";
		}
	},
	BEERMUG {
		@Override
		public String toString() {
			return "Beer Mug";
		}
	},
	METALCUP {
		@Override
		public String toString() {
			return "Metal Cup";
		}
	},
	IRISHCOFFE {
		@Override
		public String toString() {
			return "Irish Coffee";
		}
	},
	JULEPCUP {
		@Override
		public String toString() {
			return "Julep Cup";
		}
	},
	SILVERMUG {
		@Override
		public String toString() {
			return "Silver Mug";
		}
	},
	MUG {
		@Override
		public String toString() {
			return "Mug";
		}
	},
	COUPE {
		@Override
		public String toString() {
			return "Coupe";
		}
	},
	MASONJAR {
		@Override
		public String toString() {
			return "Mason Jar or Pint Glass";
		}
	},
	DOUBLEOLDFASHINED {
		@Override
		public String toString() {
			return "Double Old Fashioned";
		}
	},
	POOLCUP {
		@Override
		public String toString() {
			return "Pool Cup";
		}
	},
	PLASTICCUP {
		@Override
		public String toString() {
			return "Plastic Cup";
		}
	},
	TIKIMUG {
		@Override
		public String toString() {
			return "Tiki Mug";
		}
	},
	BRANDYSNIFTER {
		@Override
		public String toString() {
			return "Brandy Snifter";
		}
	};
	
	public static String getGlassStyle (String str) {
		String result = "";
		switch (str) {
		case "Martini" : 
			result = "MARTINI"; 
			break;
		case "Highball" : 
			result = "HIGHBALL"; 
			break;
		case "Shot" : 
			result = "SHOT"; 
			break;
		case "Old Fashioned" : 
			result = "OLDFASHIONED"; 
			break;
		case "Rocks" : 
			result = "ROCKS"; 
			break;
		case "Cocktail" : 
			result = "COCKTAIL"; 
			break;
		case "Margarita" : 
			result = "MARGARITA"; 
			break;
		case "Beer/Glass" : 
			result = "BEERGLASS"; 
			break;
		case "Champagne Flute" : 
			result = "CHAMPAGNEFLUTE"; 
			break;
		case "Pitcher" : 
			result = "PITCHER"; 
			break;
		case "Punch Bowl" : 
			result = "PUNCHBOWL"; 
			break;
		case "Copper Mug" : 
			result = "COPPERMUG"; 
			break;
		case "Other" : 
			result = "OTHER"; 
			break;
		case "Collins" : 
			result = "COLLINS"; 
			break;
		case "Cordial or Pony" : 
			result = "CORDIALPONY"; 
			break;
		case "Red Wine" : 
			result = "REDWINE"; 
			break;
		case "White Wine" : 
			result = "WHITEWINE"; 
			break;
		case "Sherry" : 
			result = "SHERRY"; 
			break;
		case "Pousse Café" : 
			result = "POUSSECAFE"; 
			break;
		case "Champange Coupe" : 
			result = "CHAMPANGECOUPE"; 
			break;
		case "Hurricane" : 
			result = "HURRICANE"; 
			break;
		case "Parfait" : 
			result = "PARFAIT"; 
			break;
		case "Moscow Mule Mug" : 
			result = "MOSCOWMULEMUG"; 
			break;
		case "Beer Mug" : 
			result = "BEERMUG"; 
			break;
		case "Metal Cup" : 
			result = "METALCUP"; 
			break;
		case "Irish Coffee" : 
			result = "IRISHCOFFE"; 
			break;
		case "Julep Cup" : 
			result = "JULEPCUP"; 
			break;
		case "Silver Mug" : 
			result = "SILVERMUG"; 
			break;
		case "Mug" : 
			result = "MUG"; 
			break;
		case "Coupe" : 
			result = "COUPE"; 
			break;
		case "Mason Jar or Pint Glass" : 
			result = "MASONJAR"; 
			break;
		case "Double Old Fashioned" : 
			result = "DOUBLEOLDFASHINED"; 
			break;
		case "Pool Cup" : 
			result = "POOLCUP"; 
			break;
		case "Plastic Cup" : 
			result = "PLASTICCUP"; 
			break;
		case "Tiki Mug" : 
			result = "TIKIMUG"; 
			break;
		case "Brandy Snifter" : 
			result = "BRANDYSNIFTER"; 
			break;
		default:
			break;
		}
		return result;
	}
}
