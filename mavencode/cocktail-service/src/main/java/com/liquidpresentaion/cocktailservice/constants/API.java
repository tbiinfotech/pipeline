package com.liquidpresentaion.cocktailservice.constants;

public final class API {

	public static final String PRESENTATION_CONTROLLER = "internal/v1/presentations";
	public static final String PRESENTATION_COCKTAIL_CONTROLLER = "internal/v1/presentations/cocktail";
	public static final String PRESENTATION_PATH_VARIABLE = "/{presentationId}";
	public static final String DUPLICATE_PRESENTATION = "/{presentationId}/duplicate";
	public static final String PATH_VARIABLE_PRESENTATION_ID = "presentationId";
	public static final String PRESENTATION_PATH_USERID = "/{userId}/presentations";
	public static final String PATH_VARIABLE_USER_ID = "userId";
	public static final String PRESENTATION_PDF = "/{presentationId}/pdf";
	public static final String PRESENTATION_CALCULATOR = "/{presentationId}/calculator";
	public static final String PRESENTATION_PPTX = "/{presentationId}/pptx";
	public static final String PRESENTATION_VIEW = "/{presentationId}/view";
	public static final String VIEW_PRESENTATION = "/view/presentation";

	public static final String COCKTAIL_CONTROLLER = "internal/v1/cocktails";
	public static final String COCKTAIL_PATH_VARIABLE = "/{cocktailId}";
	public static final String ACCESS_RIGHT = "/{cocktailId}/group";
	public static final String PATH_VARIABLE_COCKTAIL_ID = "cocktailId";
}
