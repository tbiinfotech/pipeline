package com.liquidpresentation.common.utils;

import java.util.HashMap;
import java.util.Map;

public class UomUtil {
	
	private static Map<String, Double> factorMap;
	private static final String ML        = "ML";
	private static final String CL        = "CL";
	private static final String LTR       = "LTR";
	private static final String OZ        = "OZ";
	private static final String PT        = "PT";
	private static final String DASH      = "DASH";
	private static final String SPLASH    = "SPLASH";
	private static final String TSP       = "TEA_SPOON";
	private static final String BSP       = "BAR_SPOON";
	private static final String TBSP      = "TABLE_SPOON";
	private static final String CUP       = "CUP";
	private static final String GAL       = "GAL";
	private static final String LBS       = "LBS";
	private static final String UNIT      = "UNIT";
	private static final String L         = "L";
	private static final String QT        = "QT";
	private static final String Gram      = "Gram";
	private static final String KG        = "KG";
	private static final String POUND     = "POUND";

	static {
		factorMap = new HashMap<String, Double>();
		factorMap.put(ML    ,  0.0338140);
		factorMap.put(CL    ,  0.3381402);
		factorMap.put(LTR   , 33.8140225);
		factorMap.put(OZ    ,  1.00);
		factorMap.put(PT    , 16.00);
//		factorMap.put(DASH  ,  1.00/32.00);
		factorMap.put(DASH  ,  0.03);
//		factorMap.put(SPLASH,  1.00/12.00);
		factorMap.put(SPLASH,  0.20);
//		factorMap.put(TSP,  1.00/6.00);
		factorMap.put(TSP,  0.170);
		factorMap.put(BSP   ,  0.170);
		factorMap.put(TBSP  ,  0.50);
		factorMap.put(CUP   ,  8.00);
		factorMap.put(GAL   ,128.00);
		factorMap.put(LBS   , 15.34);
		factorMap.put(POUND , 15.34);
		factorMap.put(L, 33.8140);
		factorMap.put(QT, 32.00);
		factorMap.put(Gram, 0.03);
//		factorMap.put(KG, 35.27);
	}
	
	public static double convertUom(double srcValue, String srcUom, String desUom) {
		
		Map<String, Double> factorMapLocal = new HashMap<String, Double>();
		factorMapLocal.put("ML",  0.0338140);
		factorMapLocal.put("CL"    ,  0.3381402);
		factorMapLocal.put("LTR"   , 33.8140225);
		factorMapLocal.put("OZ"    ,  1.00);
		factorMapLocal.put("PT"    , 16.00);
		factorMapLocal.put("DASH"  ,  0.03);
		factorMapLocal.put("SPLASH",  0.20);
		factorMapLocal.put("TEA_SPOON",  0.170);
		factorMapLocal.put("BAR_SPOON"   ,  0.170);
		factorMapLocal.put("TABLE_SPOON"  ,  0.50);
		factorMapLocal.put("CUP"   ,  8.00);
		factorMapLocal.put("GAL"   ,128.00);
		factorMapLocal.put("LBS"   , 15.34);
		factorMapLocal.put("POUND" , 15.34);
		factorMapLocal.put("L", 33.8140);
		factorMapLocal.put("QT", 32.00);
		factorMapLocal.put("Gram", 0.03);
		factorMapLocal.put("KG", 35.27);

		
		if(UNIT.equals(srcUom) || UNIT.equals(desUom)) 
			return srcValue;
		
		Double srcFactor = factorMapLocal.get(srcUom);
		Double desFactor = factorMapLocal.get(desUom);
		
		if(srcFactor == null || desFactor == null) 
			throw new NullPointerException("Invalid Uom");
		
		return srcFactor.doubleValue()/desFactor.doubleValue()*srcValue;
	}
	
	public static String convertToFormulaUom(String pageUom) {
		
		Map<String, String> uomMapping = new HashMap<String, String>();
		uomMapping.put("OZ","Oz");
		uomMapping.put("ML","ML");
		uomMapping.put("CL","CL");
		uomMapping.put("PT","Pt");
		uomMapping.put("LTR","L");
		uomMapping.put("DASH","Dash");
		uomMapping.put("GALLON","Gal");
		uomMapping.put("SPLASH","Splash");
		uomMapping.put("TEA_SPOON","tsp");
		uomMapping.put("BAR_SPOON","Bar Spoon");
		uomMapping.put("TABLE_SPOON","Tbsp");
		uomMapping.put("CUP","Cup");
		uomMapping.put("POUND","Lbs");
		uomMapping.put("QT","QT");
		uomMapping.put("Gram","Gram");
		uomMapping.put("KG","Kg");

		if (uomMapping.containsKey(pageUom)) {
			return uomMapping.get(pageUom);
		} else {
			return pageUom;
		}
	}

}
