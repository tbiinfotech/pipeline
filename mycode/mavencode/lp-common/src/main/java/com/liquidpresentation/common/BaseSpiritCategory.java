package com.liquidpresentation.common;

public enum BaseSpiritCategory {
	VODKA {
		@Override
		public String toString() {
			return "Vodka";
		}
	},
	FLAVOREDVODKA {
		@Override
		public String toString() {
			return "Flavored Vodka";
		}
	},
	RUM {
		@Override
		public String toString() {
			return "Rum";
		}
	},
	FLAVOREDRUM {
		@Override
		public String toString() {
			return "Flavored Rum";
		}
	},
	SPICEDRUM {
		@Override
		public String toString() {
			return "Spiced Rum";
		}
	},
	GIN {
		@Override
		public String toString() {
			return "Gin";
		}
	},
	FLAVOREDGIN {
		@Override
		public String toString() {
			return "Flavored Gin";
		}
	},
	TEQUILA {
		@Override
		public String toString() {
			return "Tequila";
		}
	},
	FLAVOREDTEQUILA {
		@Override
		public String toString() {
			return "Flavored Tequila";
		}
	},
	FLAVOREDWSK {
		@Override
		public String toString() {
			return "Flavored Whiskey";
		}
	},
	SCOTCH {
		@Override
		public String toString() {
			return "Scotch";
		}
	},
	BOURBON {
		@Override
		public String toString() {
			return "Bourbon";
		}
	},
	RYE {
		@Override
		public String toString() {
			return "Rye";
		}
	},
	CANADIANWSK {
		@Override
		public String toString() {
			return "Canadian Whiskey";
		}
	},
	IRISHWSK {
		@Override
		public String toString() {
			return "Irish Whiskey";
		}
	},
	JAPANESEWSK {
		@Override
		public String toString() {
			return "Japanese Whisky";
		}
	},
	TENNESSEEWSK {
		@Override
		public String toString() {
			return "Tennessee Whiskey";
		}
	},
	CACHACA {
		@Override
		public String toString() {
			return "Cachaca";
		}
	},
	PISCO {
		@Override
		public String toString() {
			return "Pisco";
		}
	},
	BRANDY {
		@Override
		public String toString() {
			return "Brandy/Cognac";
		}
	},
	LIQUEUR {
		@Override
		public String toString() {
			return "Liqueur/Cordial";
		}
	},
	SPARKLINGWINE {
		@Override
		public String toString() {
			return "Sparkling Wine/Champagne";
		}
	},
	WINE {
		@Override
		public String toString() {
			return "Wine";
		}
	},
	BEER {
		@Override
		public String toString() {
			return "Beer";
		}
	},
	PORT {
		@Override
		public String toString() {
			return "Port";
		}
	},
	SAKE {
		@Override
		public String toString() {
			return "Sake";
		}
	},
	SOJU {
		@Override
		public String toString() {
			return "Soju";
		}
	},
	AGEDRUM {
		@Override
		public String toString() {
			return "Aged Rum";
		}
	},
	INFUSEDVODKA {
		@Override
		public String toString() {
			return "Infused Vodka";
		}
	},
	INFUSEDGIN {
		@Override
		public String toString() {
			return "Infused Gin";
		}
	},
	INFUSEDRUM {
		@Override
		public String toString() {
			return "Infused Rum";
		}
	},
	INFUSEDTEQUILA {
		@Override
		public String toString() {
			return "Infused Tequila";
		}
	},
	INFUSEDWKS {
		@Override
		public String toString() {
			return "Infused Whiskey";
		}
	},
	AMERICANWSK {
		@Override
		public String toString() {
			return "American Whiskey";
		}
	},
	WHITEWSK {
		@Override
		public String toString() {
			return "White Whiskey";
		}
	},
	MEZAL {
		@Override
		public String toString() {
			return "Mezcal";
		}
	},
	PISCOGRAPPA {
		@Override
		public String toString() {
			return "Pisco/Grappa";
		}
	},
	BAIJIU {
		@Override
		public String toString() {
			return "Baijiu";
		}
	},
	RTD {
		@Override
		public String toString() {
			return "RTD";
		}
	},
	WHISKEY {
		@Override
		public String toString() {
			return "Whiskey";
		}
	},
	AGUARDIENTE {
		@Override
		public String toString() {
			return "Aguardiente";
		}
	},
	ASIANLIQUOR {
		@Override
		public String toString() {
			return "ASIAN - LIQUOR";
		}
	},
	EAUXDEVIE {
		@Override
		public String toString() {
			return "Eaux De Vie";
		}
	},
	MISCSPIRITS {
		@Override
		public String toString() {
			return "Misc Spirits";
		}
	},
	VERMOUTH {
		@Override
		public String toString() {
			return "Vermouth";
		}
	},
	SHOCHOU {
		@Override
		public String toString() {
			return "Shochou";
		}
	};
	
	public static String getBaseSpiritCategory (String str) {
		String result = "";
		switch (str) {
		case "Vodka" : 
			result = "VODKA"; 
			break;
		case "Flavored Vodka" : 
			result = "FLAVOREDVODKA"; 
			break;
		case "Rum" : 
			result = "RUM"; 
			break;
		case "Flavored Rum" : 
			result = "FLAVOREDRUM"; 
			break;
		case "Spiced Rum" : 
			result = "SPICEDRUM"; 
			break;
		case "Gin" : 
			result = "GIN"; 
			break;
		case "Flavored Gin" : 
			result = "FLAVOREDGIN"; 
			break;
		case "Tequila" : 
			result = "TEQUILA"; 
			break;
		case "Flavored Tequila" : 
			result = "FLAVOREDTEQUILA"; 
			break;
		case "Flavored Whiskey" : 
			result = "FLAVOREDWSK"; 
			break;
		case "Scotch" : 
			result = "SCOTCH"; 
			break;
		case "Bourbon" : 
			result = "BOURBON"; 
			break;
		case "Rye" : 
			result = "RYE"; 
			break;
		case "Canadian Whiskey" : 
			result = "CANADIANWSK"; 
			break;
		case "Irish Whiskey" : 
			result = "IRISHWSK"; 
			break;
		case "Japanese Whisky" : 
			result = "JAPANESEWSK"; 
			break;
		case "Tennessee Whiskey" : 
			result = "TENNESSEEWSK"; 
			break;
		case "Cachaca" : 
			result = "CACHACA"; 
			break;
		case "Pisco" : 
			result = "PISCO"; 
			break;
		case "Brandy/Cognac" : 
			result = "BRANDY"; 
			break;
		case "Liqueur/Cordial" : 
			result = "LIQUEUR"; 
			break;
		case "Sparkling Wine/Champagne" : 
			result = "SPARKLINGWINE"; 
			break;
		case "Wine" : 
			result = "WINE"; 
			break;
		case "Beer" : 
			result = "BEER"; 
			break;
		case "Port" : 
			result = "PORT"; 
			break;
		case "Sake" : 
			result = "SAKE"; 
			break;
		case "Soju" : 
			result = "SOJU"; 
			break;
		case "Aged Rum" : 
			result = "AGEDRUM"; 
			break;
		case "Infused Vodka" : 
			result = "INFUSEDVODKA"; 
			break;
		case "Infused Gin" : 
			result = "INFUSEDGIN"; 
			break;
		case "Infused Rum" : 
			result = "INFUSEDRUM"; 
			break;
		case "Infused Tequila" : 
			result = "INFUSEDTEQUILA"; 
			break;
		case "Infused Whiskey" : 
			result = "INFUSEDWKS"; 
			break;
		case "American Whiskey" : 
			result = "AMERICANWSK"; 
			break;
		case "White Whiskey" : 
			result = "WHITEWSK"; 
			break;
		case "Mezcal" : 
			result = "MEZAL"; 
			break;
		case "Pisco/Grappa" : 
			result = "PISCOGRAPPA"; 
			break;
		case "Baijiu" : 
			result = "BAIJIU"; 
			break;
		case "RTD" : 
			result = "RTD"; 
			break;
		case "Whiskey" : 
			result = "WHISKEY"; 
			break;
		case "Aguardiente" : 
			result = "AGUARDIENTE"; 
			break;
		case "ASIAN - LIQUOR" : 
			result = "ASIANLIQUOR"; 
			break;
		case "Eaux De Vie" : 
			result = "EAUXDEVIE"; 
			break;
		case "Misc Spirits" : 
			result = "MISCSPIRITS"; 
			break;
		case "Vermouth" : 
			result = "VERMOUTH"; 
			break;
		case "Shochou" : 
			result = "SHOCHOU"; 
			break;
		default:
			break;
		}
		return result;
	}
}
