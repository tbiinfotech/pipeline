package com.liquidpresentaion.cocktailservice.specification;

public enum FilterKey {
	Brand {
		@Override
		public String toString() {
			return "Brand";
		}
	},
	BaseSpiritCategory {
		@Override
		public String toString() {
			return "BaseSpiritCategory";
		}
	},
	BaseSpiritModifier {
		@Override
		public String toString() {
			return "BaseSpiritModifier";
		}
	},
	JuiceLiquids {
		@Override
		public String toString() {
			return "JuiceLiquids";
		}
	},
	Sweetener {
		@Override
		public String toString() {
			return "Sweetener";
		}
	},
	Solids {
		@Override
		public String toString() {
			return "Solids";
		}
	},
	NumberOfIngredients {
		@Override
		public String toString() {
			return "NumberOfIngredients";
		}
	},
	DegreeOfDifficulty {
		@Override
		public String toString() {
			return "DegreeOfDifficulty";
		}
	},
	CocktailCategory {
		@Override
		public String toString() {
			return "CocktailCategory";
		}
	},
	SeasonalThemed {
		@Override
		public String toString() {
			return "SeasonalThemed";
		}
	},
	FlavorProfile {
		@Override
		public String toString() {
			return "FlavorProfile";
		}
	},
	CocktailGlassStyle {
		@Override
		public String toString() {
			return "CocktailGlassStyle";
		}
	},
	OutletType {
		@Override
		public String toString() {
			return "OutletType";
		}
	},
	NameOfCocktail {
		@Override
		public String toString() {
			return "NameOfCocktail";
		}
	},
	Supplier {
		@Override
		public String toString() {
			return "Supplier";
		}
	}
}
