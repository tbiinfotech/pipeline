package com.liquidpresentaion.users.model;

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
	}
}
