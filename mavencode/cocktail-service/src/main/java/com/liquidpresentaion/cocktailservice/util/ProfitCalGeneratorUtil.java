package com.liquidpresentaion.cocktailservice.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataValidationConstraint.ValidationType;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.SheetVisibility;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.liquidpresentaion.cocktailservice.model.Cocktail;
import com.liquidpresentaion.cocktailservice.model.CocktailBrand;
import com.liquidpresentaion.cocktailservice.model.ExcelBrand;
import com.liquidpresentaion.cocktailservice.model.HousemadeBrand;
import com.liquidpresentaion.cocktailservice.model.Ingredient;
import com.liquidpresentaion.cocktailservice.model.Presentation;
import com.liquidpresentaion.cocktailservice.model.Price;
import com.liquidpresentaion.cocktailservice.repository.BrandRepository;
import com.liquidpresentaion.cocktailservice.repository.HousemadeBrandRepository;
import com.liquidpresentaion.cocktailservice.repository.IngredientRepository;
import com.liquidpresentation.common.BaseSpiritCategory;
import com.liquidpresentation.common.Category;
import com.liquidpresentation.common.IngredientType;
import com.liquidpresentation.common.utils.StringUtil;
import com.liquidpresentation.common.utils.UomUtil;

@Component
public class ProfitCalGeneratorUtil {
		
	@Value("${profitCalculator.protectPassword}")
	private static String protectPassword;
	
	@Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private HousemadeBrandRepository housemadeBrandRepository;
    @Autowired
    private BrandRepository brandRepository;
	
	private static final CellCopyPolicy COPY_POLICY = new CellCopyPolicy();
	private static final String UOM_UNIT = "UNIT";
	private static final String UOM_OZ = "OZ";
	private static final int FORECAST_COCKTAIL_STARTROW = 12;
	private static final int[] COCKTAIL_STARTROWS = {13, 21, 29, 37, 45, 53, 60};
	
	private static final int[] INGREDIENT_STARTROWS = {7, 13, 19, 25, 31};
	//forecast formulas
	private static final String FORECAST_MENU_PRICE_TO_HIT_MARGIN = "F%d/(C4 + C7)";
	private static final String FORECAST_COCKTAIL_COST = "'%s'!D5";
	private static final String FORECAST_COCKTAIL_GPS = "D%1$d-F%1$d";
	private static final String FORECAST_COCKTAIL_GPP = "(G%1$d/D%1$d) - C7";
	private static final String FORECAST_SF_NET_SALES = "J%1$d*D%1$d";
	private static final String FORECAST_SF_TOTAL_COST = "J%1$d*F%1$d";
	private static final String FORECAST_SF_NET_GPS = "K%1$d-L%1$d";
	private static final String FORECAST_SF_NET_GPP = "(M%1$d/K%1$d) - C7";
	private static final String FORECAST_SF_SOLD_TOTAL = "SUM(J13:J%d)";
	private static final String FORECAST_SF_NET_SALES_TOTAL = "SUM(K13:K%d)";
	private static final String FORECAST_SF_TOTAL_COST_TOTAL = "SUM(L13:L%d)";
	private static final String FORECAST_COS = "L%1$d/K%1$d";
	private static final String FORECAST_GPS = "M%d";
	//cocktail formulas
	private static final String COCKTAIL_MENU_PRICE = "'Sales Forecast'!D%d";
	private static final String COCKTAIL_COST_OZ = "C%1$d*'Total Ingredient List'!P%2$d";
	private static final String COCKTAIL_COST_UNIT = "C%1$d*'Total Ingredient List'!P%2$d";
	private static final String COCKTAIL_SUB_TOTAL = "SUM(E%1$d:K%2$d)";
	private static final String COCKTAIL_PORTION_SUB_TOTAL = "SUMIFS(C%1$d:C%2$d,D%1$d:D%2$d,\"<>Unit\")";
	private static final String COCKTAIL_PORTION = "%f*INDEX('UOM Conversion Table'!$A4:$S22,MATCH(\"oz\",'UOM Conversion Table'!A4:$S4,0), MATCH(D%d,'UOM Conversion Table'!A4:A22,0))";
	//ingredient formulas
	private static final String INGREDIENT_USED_REF = "'%1$s'!C%2$d*'Sales Forecast'!J%3$d";
	private static final String INGREDIENT_BOTTLE_USED = "IFERROR((((SUM(%1$s))))/F%2$d/INDEX('UOM Conversion Table'!A4:S22,MATCH(G%2$d,'UOM Conversion Table'!A4:A22,0), MATCH(\"oz\",'UOM Conversion Table'!A4:S4,0)),\"N/A\")";
	private static final String INGREDIENT_UNIT_USED = "IFERROR((((SUM(%1$s)))),\"N/A\")";
	private static final String INGREDIENT_PRICE_CASADE = "INDEX('B%1$d'!$%4$s1:$%4$s%2$d,MATCH(E%3$d,'B%1$d'!$A1:$A%2$d,0))";
	private static final String INGREDIENT_DESCRIPTION_CASADE = "INDEX('B%1$d'!$%4$s1:$%4$s%2$d,MATCH(D%3$d,'B%1$d'!$F1:$F%2$d,0))";
	private static final String INGREDIENT_DESCRIPTION_DROPDOWN = "'B%1$d'!$%2$s1:$%2$s%3$d";
	private static final String INGREDIENT_HOUSEMADE_COST = "E%1$d*C%2$d/IF(C%1$d=0,1,C%1$d)";
	private static final String INGREDIENT_COST_OZ = "IFERROR(N%1$d/(F%1$d*INDEX('UOM Conversion Table'!$A4:$S22,MATCH(G%1$d,'UOM Conversion Table'!A4:$A22,0),MATCH(\"oz\",'UOM Conversion Table'!A4:S4,0))),0)";
	private static final String INGREDIENT_COST_UNIT = "IFERROR(N%1$d/(F%1$d*INDEX('UOM Conversion Table'!$A4:$S22,MATCH(G%1$d,'UOM Conversion Table'!A4:$A22,0),MATCH(\"Unit\",'UOM Conversion Table'!A4:S4,0))),0)";
	
	private String cocktailCostOz;
	private String cocktailPortion;
	private String ingredientBottleUsed;
	private String ingredientCostOz;
	
    private static void protect(XSSFWorkbook wb) {
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            XSSFSheet s = wb.getSheetAt(i);
            s.lockFormatColumns(false);
            s.lockFormatRows(false);
            s.protectSheet(protectPassword);
        }
    }
    
    public ByteArrayInputStream createProfitCal(String templateName, Presentation data, Map<Integer, ExcelBrand> brandPrices) throws IOException {
    	
    	String presentationUOM = data.getUom();
    	this.cocktailCostOz = COCKTAIL_COST_OZ;
    	this.cocktailPortion = COCKTAIL_PORTION;
    	this.ingredientBottleUsed = INGREDIENT_BOTTLE_USED;
    	this.ingredientCostOz = INGREDIENT_COST_OZ;
    	if (StringUtil.isNotEmpty(presentationUOM)) {
    		cocktailCostOz = cocktailCostOz.replace("oz", presentationUOM);
//    		cocktailPortion = cocktailPortion.replace("oz", presentationUOM);
    		ingredientBottleUsed = ingredientBottleUsed.replace("oz", presentationUOM);
    		ingredientCostOz = ingredientCostOz.replace("oz", presentationUOM);
		}
    	
    	
    	Assert.notNull(templateName, "The templateName can not be null");
    	XSSFWorkbook wb;
		try {
			//load template xlsx from classpath
			//Resource tpl = new ClassPathResource("\\templates\\"+templateName+".xlsx");
			//wb = new XSSFWorkbook(tpl.getFile());
			//wb = new XSSFWorkbook("H:\\Work\\Local\\Code\\lpsolustions\\cocktail-service\\src\\main\\resources\\templates\\ProfitCalculatorTpl.xlsx");
			InputStream in = Resource.class.getResourceAsStream("/templates/" + templateName + ".xlsx");
			wb = new XSSFWorkbook(in);
			
			//sales forecast sheet
			XSSFSheet fs = wb.getSheetAt(0);
			int cocktailCnt = data.getCocktails().size();
			if(cocktailCnt>1) {
				insertRows(fs, FORECAST_COCKTAIL_STARTROW + 1, cocktailCnt-1);
			}			
			
			//add brands without prices to map and fill category
			addBrandCategory(data.getCocktails(), brandPrices);
			//pre fill the brand and prices
			prefillIngredientSheet(wb, brandPrices, data.getCocktails(), data.getUom());
			
			//process cocktail sheets
			Map<String, Long> ctNameCntMap = data.getCocktails().stream().filter(ctail -> StringUtils.isNotEmpty(ctail.getName()))
																.map(ctail -> ctail.getName().length() >= 31 ? ctail.getName().substring(0, 31) : ctail.getName())
																.collect(Collectors.groupingBy(objStr -> objStr, Collectors.counting())); // group by the name with same pre 31 char(or less than 31:itself)
			Map<String, Integer> sheetNameCntMap = new HashMap<>();
			for(int i=0;i<cocktailCnt;i++) {
				Cocktail cocktail = data.getCocktails().get(i); // get current cocktail
				String ctName31Len = cocktail.getName() != null && cocktail.getName().length() >= 31 ? cocktail.getName().substring(0, 31) : cocktail.getName();
				String cocktailSheetName = ctName31Len; // init cocktail sheet name
				if(ctNameCntMap.get(ctName31Len) != null && ctNameCntMap.get(ctName31Len) > 1) { // name in map and the count of the name over one
					sheetNameCntMap.put(ctName31Len, sheetNameCntMap.get(ctName31Len) != null ? sheetNameCntMap.get(ctName31Len) + 1 : 1); // record used index of the same name
					cocktailSheetName = (cocktail.getName().length() >= 29 ? cocktail.getName().substring(0, 29) : cocktail.getName()) + "~" + sheetNameCntMap.get(ctName31Len); // prepare cocktail sheet name
				}
				processCocktail(wb, fs, data.getCocktails().get(i), data.getUom(), i, brandPrices, cocktailSheetName);
			}
			
			//sales forecast total row
			int ttCodeRow = FORECAST_COCKTAIL_STARTROW+cocktailCnt;
			int ttRealRow = ttCodeRow + 1;
			XSSFRow fsRow = fs.getRow(ttCodeRow);			
			fsRow.getCell(9).setCellFormula(String.format(FORECAST_SF_SOLD_TOTAL, ttCodeRow));
			fsRow.getCell(10).setCellFormula(String.format(FORECAST_SF_NET_SALES_TOTAL, ttCodeRow));
			fsRow.getCell(11).setCellFormula(String.format(FORECAST_SF_TOTAL_COST_TOTAL, ttCodeRow));
			fsRow.getCell(12).setCellFormula(String.format(FORECAST_SF_NET_GPS, ttRealRow));
			fsRow.getCell(13).setCellFormula(String.format(FORECAST_SF_NET_GPP, ttRealRow));
			
			
			XSSFCellStyle boldTextStyle = wb.createCellStyle();
			boldTextStyle.cloneStyleFrom(fsRow.getCell(9).getCellStyle());
			
			XSSFFont font = wb.createFont();
		    font.setBold(true);
		    boldTextStyle.setFont(font);
		    
			fsRow.getCell(9).setCellStyle(boldTextStyle);
			
			XSSFCellStyle boldDollarStyle = wb.createCellStyle();
			boldDollarStyle.cloneStyleFrom(fsRow.getCell(10).getCellStyle());
			boldDollarStyle.setFont(font);
			
			
			fsRow.getCell(10).setCellStyle(boldDollarStyle);
			fsRow.getCell(11).setCellStyle(boldDollarStyle);
			fsRow.getCell(12).setCellStyle(boldDollarStyle);
			
			
			XSSFCellStyle boldPercentStyle = wb.createCellStyle();
			boldPercentStyle.cloneStyleFrom(fsRow.getCell(13).getCellStyle());
			boldPercentStyle.setFont(font);
			fsRow.getCell(13).setCellStyle(boldPercentStyle);
			
			//overall cos and gp$
			fs.getRow(3).getCell(5).setCellFormula(String.format(FORECAST_COS, ttRealRow));
			fs.getRow(5).getCell(5).setCellFormula(String.format(FORECAST_GPS, ttRealRow));
			
			//remove cocktail sheet template
			wb.removeSheetAt(1);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException("invalid template file");
		}
    	
    	protect(wb);
    	ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
    	wb.write(baos);
    	return new ByteArrayInputStream(baos.toByteArray());
    }
    
	/**
     * 
     * @param sheet
     * @param tplRow the template row index, inserted rows under this row and will keep the style with this row
     * @param rows number of rows to be inserted
     */
    private static void insertRows(XSSFSheet sheet, int tplRow, int rows) {
    	sheet.shiftRows(tplRow + 1, sheet.getLastRowNum(), rows, true, false);
    	XSSFRow tpl = sheet.getRow(tplRow);
    	for(int i=0;i<rows;i++) {
    		XSSFRow row = sheet.createRow(tplRow+1+i);
    		row.copyRowFrom(tpl, COPY_POLICY);
    	}
    }
    
    private void insertIngredientRows(XSSFSheet sheet, int tplRow, int rows, int tplNum) {
    	sheet.shiftRows(tplRow, sheet.getLastRowNum(), rows, true, false);
    	XSSFRow tpl = sheet.getRow(sheet.getLastRowNum() - 4 + tplNum);
    	for(int i=0;i<rows;i++) {
    		XSSFRow row = sheet.createRow(tplRow+i);
    		row.copyRowFrom(tpl, COPY_POLICY);
    		
    		//OZ cost
    		if (tplNum > 2) {//3: Solids; 4: Garnish
    			row.getCell(15).setCellFormula(String.format(INGREDIENT_COST_UNIT, row.getRowNum() + 1));
			} else {
    			row.getCell(15).setCellFormula(String.format(ingredientCostOz, row.getRowNum() + 1));
			}
    	}
    }
    
   private static void insertHousemadeRows(XSSFSheet sheet, int tplRow) {
	   sheet.copyRows(tplRow-3, tplRow+3, tplRow+3, COPY_POLICY);
   }
    
    private void processCocktail(XSSFWorkbook wb, XSSFSheet fs, Cocktail ct, String calUom, int i, Map<Integer, ExcelBrand> brandPrices, String... cocktailSheetName) {
    	//clone from cocktail sheet template
		XSSFSheet cs = wb.cloneSheet(1);
		//ingredient sheet
		XSSFSheet is = wb.getSheetAt(2+i);
		//set sheet name with cocktail name
		String cSheetName = cocktailSheetName == null || StringUtil.isAllEmpty(cocktailSheetName) ? ct.getName() : cocktailSheetName[0];
		// cName = cSheetName.replace("'", "''");
//		cSheetName = cSheetName.replaceFirst("^'*", "");
		cSheetName = cSheetName.replace("'", "").replace("\"", "");
		String cName = cSheetName;
		wb.setSheetName(wb.getSheetIndex(cs), cSheetName);
		//forecast sheet cocktail details & formula
		int fsCodeRow = FORECAST_COCKTAIL_STARTROW+i;
		int fsRealRow = fsCodeRow + 1;
		XSSFRow fsRow = fs.getRow(fsCodeRow);
		fsRow.getCell(1).setCellValue(ct.getName());
		fsRow.getCell(2).setCellValue(ct.getBrandName());
		fsRow.getCell(4).setCellFormula(String.format(FORECAST_MENU_PRICE_TO_HIT_MARGIN, fsRealRow));
		fsRow.getCell(5).setCellFormula(String.format(FORECAST_COCKTAIL_COST, cName));
		fsRow.getCell(6).setCellFormula(String.format(FORECAST_COCKTAIL_GPS, fsRealRow));
		fsRow.getCell(7).setCellFormula(String.format(FORECAST_COCKTAIL_GPP, fsRealRow));
		fsRow.getCell(9).setCellValue(1);
		fsRow.getCell(10).setCellFormula(String.format(FORECAST_SF_NET_SALES, fsRealRow));
		fsRow.getCell(11).setCellFormula(String.format(FORECAST_SF_TOTAL_COST, fsRealRow));
		fsRow.getCell(12).setCellFormula(String.format(FORECAST_SF_NET_GPS, fsRealRow));
		fsRow.getCell(13).setCellFormula(String.format(FORECAST_SF_NET_GPP, fsRealRow));
		//cell A2 name
		cs.getRow(1).getCell(0).setCellValue(ct.getName());
		cs.getRow(3).getCell(3).setCellFormula(String.format(COCKTAIL_MENU_PRICE, fsRealRow));
		//iterator cocktail brands
		List<CocktailBrand> base = new ArrayList<CocktailBrand>();
		List<CocktailBrand> juice = new ArrayList<CocktailBrand>();
		List<CocktailBrand> sweet = new ArrayList<CocktailBrand>();
		List<CocktailBrand> solids = new ArrayList<CocktailBrand>();
		List<CocktailBrand> garnish = new ArrayList<CocktailBrand>();
		List<CocktailBrand> housemade = new ArrayList<CocktailBrand>();
		Iterator<CocktailBrand> iterator = ct.getBrandSet().iterator();
		while(iterator.hasNext()) {
			CocktailBrand cb = iterator.next();
			Category cate = cb.getCategory();
			if(cate != null) {
				switch (cate){
					case BASESPIRIT:
						base.add(cb);
						break;
					case JUICE:
						juice.add(cb);
						break;
					case SWEETENER:
						sweet.add(cb);
						break;
					case SOLIDS:
						solids.add(cb);
						break;
					case GARNISH:
						garnish.add(cb);
						break;
					case HOUSEMADE:
						housemade.add(cb);
						break;
					default:
						//house.add(cb);
				}
			}
		}
		//process base
		//calculate cocktail sheet row shift
		int cRowShift = 0;
		int cStartRow = COCKTAIL_STARTROWS[0] + cRowShift;
		//do the row shift and save the shifts
		if(base.size()>1) {
			insertRows(cs, cStartRow, base.size()-1);
			cRowShift += (base.size()-1);
		}
		this.fillCocktailBrand(cs, is, base, calUom, cStartRow, cName, fsRealRow, brandPrices);
		//base sub total
		int baseSubRow = COCKTAIL_STARTROWS[0] + cRowShift + 1;
		cs.getRow(baseSubRow).getCell(4).setCellFormula(String.format(COCKTAIL_SUB_TOTAL, cStartRow+1, baseSubRow));
		
		//process juice
		//calculate cocktail sheet row shift
		cStartRow = COCKTAIL_STARTROWS[1] + cRowShift;
		//do the row shift and save the shifts
		if(juice.size()>1) {
			insertRows(cs, cStartRow, juice.size()-1);
			cRowShift += (juice.size()-1);
		}
		
		//data & formula filling
		this.fillCocktailBrand(cs, is, juice, calUom, cStartRow, cName, fsRealRow, brandPrices);
		//juice sub total
		int juiceSubRow = COCKTAIL_STARTROWS[1] + cRowShift + 1;
		cs.getRow(juiceSubRow).getCell(4).setCellFormula(String.format(COCKTAIL_SUB_TOTAL, cStartRow+1, juiceSubRow));
		
		//process sweetener
		//calculate cocktail sheet row shift
		cStartRow = COCKTAIL_STARTROWS[2] + cRowShift;
		//do the row shift and save the shifts
		if(sweet.size()>1) {
			insertRows(cs, cStartRow, sweet.size()-1);
			cRowShift += (sweet.size()-1);
		}
		//data & formula filling
		this.fillCocktailBrand(cs, is, sweet, calUom, cStartRow, cName, fsRealRow, brandPrices);
		//sweetener sub total
		int sweetSubRow = COCKTAIL_STARTROWS[2] + cRowShift + 1;
		cs.getRow(sweetSubRow).getCell(4).setCellFormula(String.format(COCKTAIL_SUB_TOTAL, cStartRow+1, sweetSubRow));
		
		//process solids
		//calculate cocktail sheet row shift
		cStartRow = COCKTAIL_STARTROWS[3] + cRowShift;
		//do the row shift and save the shifts
		if(solids.size()>1) {
			insertRows(cs, cStartRow, solids.size()-1);
			cRowShift += (solids.size()-1);
		}
		
		//data & formula filling
		this.fillCocktailBrand(cs, is, solids, calUom, cStartRow, cName, fsRealRow, brandPrices);
		//solids sub total
		int solidSubRow = COCKTAIL_STARTROWS[3] + cRowShift + 1;
		cs.getRow(solidSubRow).getCell(4).setCellFormula(String.format(COCKTAIL_SUB_TOTAL, cStartRow+1, solidSubRow));
		
		//process garnish
		//calculate cocktail sheet row shift
		cStartRow = COCKTAIL_STARTROWS[4] + cRowShift;
		//do the row shift and save the shifts
		if(garnish.size()>1) {
			insertRows(cs, cStartRow, garnish.size()-1);
			cRowShift += (garnish.size()-1);
		}
		
		//data & formula filling
		this.fillCocktailBrand(cs, is, garnish, calUom, cStartRow, cName, fsRealRow, brandPrices);
		//garnish sub total
		int garnishSubRow = COCKTAIL_STARTROWS[4] + cRowShift + 1;
		cs.getRow(garnishSubRow).getCell(4).setCellFormula(String.format(COCKTAIL_SUB_TOTAL, cStartRow+1, garnishSubRow));
		
		//process housemade
		//calculate cocktail sheet row shift
		cStartRow = COCKTAIL_STARTROWS[5] + cRowShift;
		//do the row shift and save the shifts
		if(housemade.size()>1) {
			insertRows(cs, cStartRow, housemade.size()-1);
			cRowShift += (housemade.size()-1);
		}

		//housemade sub total
		int housemadeSubRow = COCKTAIL_STARTROWS[5] + cRowShift + 1;
		cs.getRow(housemadeSubRow).getCell(4).setCellFormula(String.format(COCKTAIL_SUB_TOTAL, cStartRow+1, housemadeSubRow));

		//data & formula filling
		this.fillCocktailBrand(cs, is, housemade, calUom, cStartRow, cName, fsRealRow, brandPrices);
		final int housemadeStartRow = cStartRow;
		cStartRow = COCKTAIL_STARTROWS[6] + cRowShift;
        for (int hm = 0;hm<housemade.size();hm++) {
        	int cnt = 0;
        	List<HousemadeBrand> housemadeBrands = brandPrices.get(housemade.get(hm).getBrandPkId()) == null ? null : brandPrices.get(housemade.get(hm).getBrandPkId()).getHousemadeBrands();
        	if(hm != housemade.size()-1) {
        		insertHousemadeRows(cs, cStartRow);
        	}
            if (!CollectionUtils.isEmpty(housemadeBrands) && housemadeBrands.size() > 1) {
            	insertRows(cs, cStartRow, housemadeBrands.size()-1);
                cRowShift += (housemadeBrands.size()-1);
                cnt += (housemadeBrands.size()-1);
			}
            cs.getRow(cStartRow-3).getCell(1).setCellValue(housemade.get(hm).getBrandName());
            //data & formula filling
            this.fillHouseBrand(cs, is, housemadeBrands, calUom, cStartRow, cName, fsRealRow, brandPrices);
            int housemadeBrandSubRow = cStartRow + housemadeBrands.size() + 1;
            cs.getRow(housemadeBrandSubRow-1).getCell(4).setCellFormula(String.format(COCKTAIL_SUB_TOTAL, cStartRow + 1, housemadeBrandSubRow-1));
            // hidden calculation in the sub total.
            if (!CollectionUtils.isEmpty(housemadeBrands)) {
            	StringBuffer sumHousemadePortion = new StringBuffer();
            	
                for (int housemadeRow = 0; housemadeRow < housemadeBrands.size(); housemadeRow++) {
                	if (!UOM_UNIT.equals(housemadeBrands.get(housemadeRow).getIhUom())) {
                		if (!sumHousemadePortion.toString().contains("SUM(")) {
                			sumHousemadePortion.append("SUM(");
                		}
                		sumHousemadePortion.append("C"+(cStartRow+1+housemadeRow)+",");
    				}
                }
                if (sumHousemadePortion.toString().contains("SUM")) {
                	int lastIndex = sumHousemadePortion.lastIndexOf(",");
        			sumHousemadePortion.replace(lastIndex, sumHousemadePortion.length(), ")");
                    cs.getRow(housemadeBrandSubRow-1).getCell(2).setCellFormula(sumHousemadePortion.toString());
				}
			}
            cs.getRow(housemadeStartRow+hm).getCell(4).setCellFormula(String.format(INGREDIENT_HOUSEMADE_COST,housemadeBrandSubRow,housemadeStartRow+hm+1));
            cStartRow = cStartRow + cnt + 6;
        }
        
        wb.setSheetOrder(cSheetName, i+2);
    }
    
    private void fillCocktailBrand(XSSFSheet cs, XSSFSheet is, List<CocktailBrand> list, String calUom, int cStartRow, String cName, int fsRealRow, Map<Integer, ExcelBrand> brandPrices) {
    	XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(cs);
        XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper.createFormulaListConstraint("uoms_val");
    	for(int c=0;c<list.size();c++) {
	    	CocktailBrand cb = list.get(c);
			int cCodeRow = cStartRow + c;
			int cRealRow = cCodeRow + 1;
			int iCodeRow = brandPrices.get(cb.getBrandPkId()).getRow();
			int iRealRow = iCodeRow + 1;
			//cocktail sheet filling
			XSSFRow cBaseRow = cs.getRow(cCodeRow);
			cBaseRow.getCell(1).setCellValue(cb.getBrandName());
			//no calculation on unit/solids/garnish
			String brandUom = cb.getUom();
			if(UOM_UNIT.equals(brandUom) /*|| StringUtil.isAllEmpty(brandUom) 
					|| Category.SOLIDS.equals(cb.getCategory()) || Category.GARNISH.equals(cb.getCategory())*/) {
				cBaseRow.getCell(2).setCellValue(cb.getQuantity());
				cBaseRow.getCell(3).setCellValue(brandUom);
				cBaseRow.getCell(4).setCellFormula(String.format(COCKTAIL_COST_UNIT, cRealRow, iRealRow));
			} else {
				//portion formula to calculate on oz value/cost
//				double ozValue = UomUtil.convertUom(cb.getQuantity(), brandUom, UOM_OZ);
//				cBaseRow.getCell(2).setCellFormula(String.format(cocktailPortion, ozValue, cRealRow));
//				cBaseRow.getCell(3).setCellValue(calUom);
//				cBaseRow.getCell(4).setCellFormula(String.format(cocktailCostOz, cRealRow, iRealRow));
				
				//Jira 414 #2
				String formulaUom = UomUtil.convertToFormulaUom(brandUom);
				String cocktailPortionFormula = String.format(cocktailPortion, cb.getQuantity(), cRealRow).replace("oz", formulaUom);
				cBaseRow.getCell(2).setCellFormula(cocktailPortionFormula);
				cBaseRow.getCell(3).setCellValue(UomUtil.convertToFormulaUom(calUom));
				cBaseRow.getCell(4).setCellFormula(String.format(cocktailCostOz, cRealRow, iRealRow));
				
			}
			//add uom  list
			System.out.println(cs.getSheetName()+" creating dropdown list on row " + cCodeRow);
			XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, new CellRangeAddressList(cCodeRow, cCodeRow, 3, 3));
			validation.setSuppressDropDownArrow(true);
            validation.setShowErrorBox(false);
            cs.addValidationData(validation);
            
			//ingredient sheet : fill Quantity used
            if (iCodeRow > 0) {
            	XSSFRow iBaseRow = is.getRow(iCodeRow);
    			XSSFCell cell = iBaseRow.getCell(14);
    			String reference = String.format(INGREDIENT_USED_REF, cName, cRealRow, fsRealRow);
    			if (cell.getCellTypeEnum() == CellType.BLANK) {
    				if(UOM_UNIT.equals(brandUom)) {
    					cell.setCellFormula(String.format(INGREDIENT_UNIT_USED, reference));
    				} else {
    					cell.setCellFormula(String.format(ingredientBottleUsed, reference, iRealRow));
    				}
    			} else {
    				/*
    				 * Housemade is excluded from ingredient list page so can not calcuate its cost
    				 * This is a potential defect in ProfitCalc template
    				 */
    				if (!Category.HOUSEMADE.equals(cb.getCategory())) {
    					String formula = cell.getCellFormula();
    					int index  = formula.indexOf("))))");
    					formula = formula.substring(0, index) + "," + reference + formula.substring(index, formula.length());
    					System.out.println(formula);
    					cell.setCellFormula(formula);
    				}
    			}
			}
    	}
    }
    
    private void prefillIngredientSheet(XSSFWorkbook wb, Map<Integer, ExcelBrand> brandPrices, List<Cocktail> cocktails, String presentationUOM ) {
    	DecimalFormat dFormat = new DecimalFormat("#.##");
    	XSSFSheet is = wb.getSheet("Total Ingredient List");
    	setupColumnHeaders(is, presentationUOM);
    	createIngredientSheetRows(is, brandPrices);
    	for(int id : brandPrices.keySet()) {
    		String displayUom = null;
    		ExcelBrand brand = brandPrices.get(id);
    		Price selected = null;
    		int pSize = brand.getPrices().size();
    		String[] priceTitles = null;			
    		if(pSize>0) {//This if block is to create hidden sheet for brands which have prices and determine the default display price;
    			priceTitles = new String[pSize];
    			XSSFSheet sheet = wb.createSheet("B"+id);
    			wb.setSheetVisibility(wb.getSheetIndex(sheet), SheetVisibility.VERY_HIDDEN);
	    		for(int i=0;i<brand.getPrices().size();i++) {
	    			Price price = brand.getPrices().get(i);
	    			String priceTitle = price.getPrSize().intValue() + " " + price.getPrUom();
	    			priceTitles[i] = priceTitle;
	    			//choose default selected price
	    			if(selected == null) {
	    				selected = price;
	    			} else {
	    				double selectedML =  UomUtil.convertUom(selected.getPrSize().doubleValue(), selected.getPrUom(), "ML");
	    				double currentML = UomUtil.convertUom(price.getPrSize().doubleValue(), price.getPrUom(), "ML");
	    				//same with selected, choose 12 case pack
	    				if (Math.abs(currentML-selectedML) <= 0.001) {
	    					if(price.getPrCasePack().intValue() == 12) {
	    						selected = price;
	    					}
	    				}
	    				//select 1L
	    				else if (Math.abs(currentML-1000.00) <= 0.001) {
	    					selected = price;
	    				} else if (Math.abs(selectedML-1000.00) > 0.001 && currentML < 1000.00 && selectedML > 1000.00){
	    					selected = price;
	    				}
	    				//1000 > current > selected, choose current
	    				else if (Math.abs(selectedML-1000.00) > 0.001 && selectedML < 1000.00 && currentML < 1000.00 && currentML > selectedML){
	    					selected = price;
						}
	    				//1000 < current < selected, choose current
	    				else if (Math.abs(selectedML-1000.00) > 0.001 && selectedML > 1000.00 && currentML > 1000.00 && currentML < selectedML){
	    					selected = price;
						}    				
	    			}
	        		XSSFRow pRow = sheet.createRow(i);
	        		pRow.createCell(0).setCellValue(priceTitle);
	        		pRow.createCell(1).setCellValue(price.getPrSize().intValue());
	        		pRow.createCell(2).setCellValue(price.getPrUom());
	        		pRow.createCell(3).setCellValue(price.getPrCasePack().intValue());
	        		pRow.createCell(4).setCellValue(price.getPrCasePrice().doubleValue());
	        		pRow.createCell(5).setCellValue(price.getPrDistribtorItemCode() + ", " + price.getPrDescription() + ", " + dFormat.format(price.getPrSize().doubleValue()) + price.getPrUom() + ", " + price.getPrCasePack() + " pk");
	        		pRow.createCell(6).setCellValue(price.getPrDistribtorItemCode()); // add column G: distributor item code
	        		pRow.createCell(7).setCellValue(price.getPrBaseCategory() == null ? null : price.getPrBaseCategory().toString()); // add column H: base spirit category
	        		pRow.createCell(8).setCellValue(price.getPrSalesGroupPkid());
	        		pRow.createCell(9).setCellValue(price.getPrMpc());
	        		pRow.createCell(10).setCellValue(price.getBrandPkid());
	        		pRow.createCell(11).setCellValue(price.getPrIngredientPkid());
	        		pRow.createCell(12).setCellValue(price.getPkId());
	        		
	    		}
	    		displayUom = selected.getPrUom();
    		}
    		
    		//Logic below is to set formula cells and those brands which do not have any price info;
    		int rowId = brand.getRow();    		
    		XSSFRow row = is.getRow(rowId);
    		System.out.println("brand " + id + " " + brand.getBrandCategory() + " at row " + rowId);
    		row.getCell(2).setCellValue("#N/A");

    		if(pSize>0) {
	    		String strFormula = String.format(INGREDIENT_DESCRIPTION_DROPDOWN, id, "F", pSize);
	    		XSSFDataValidationConstraint constraint = new XSSFDataValidationConstraint(ValidationType.LIST, strFormula);
	    		XSSFDataValidationHelper help = new XSSFDataValidationHelper(is);
	    		XSSFDataValidation dataValidation = (XSSFDataValidation) help.createValidation(constraint, new CellRangeAddressList(rowId, rowId, 3, 3));
	    		dataValidation.setSuppressDropDownArrow(true);
	    		dataValidation.setShowErrorBox(false);
	    		is.addValidationData(dataValidation);
	    		row.getCell(3).setCellValue(selected.getPrDistribtorItemCode() + ", " + selected.getPrDescription() + ", " + dFormat.format(selected.getPrSize().doubleValue()) + selected.getPrUom() + ", " + selected.getPrCasePack() + " pk");
				if("BASESPIRIT".equals(selected.getPrCategory().name())) {
					row.getCell(4).setCellFormula(String.format(INGREDIENT_DESCRIPTION_CASADE, id, pSize, rowId+1, "H"));
				}
	    		row.getCell(5).setCellFormula(String.format(INGREDIENT_DESCRIPTION_CASADE, id, pSize, rowId+1, "B"));
	    		row.getCell(6).setCellFormula(String.format(INGREDIENT_DESCRIPTION_CASADE, id, pSize, rowId+1, "C"));
	    		row.getCell(7).setCellFormula(String.format(INGREDIENT_DESCRIPTION_CASADE, id, pSize, rowId+1, "D"));
	    		row.getCell(8).setCellFormula(String.format(INGREDIENT_DESCRIPTION_CASADE, id, pSize, rowId+1, "E"));
	    		row.getCell(2).setCellFormula(String.format(INGREDIENT_DESCRIPTION_CASADE, id, pSize, rowId+1, "G"));
    		} else {
    			/*
    			 * #N/A for item code, Description will just be (Brand Name), 
    			 * Base Spirit Category if it is a Base Spirit/Modifier, 
    			 * Size Blank, 
    			 * UOM Blank, 
    			 * Case Pack Blank, 
    			 * Case Price 0, 
    			 * Bottle Price 0, 
    			 * Discount 0, 
    			 * Net case 0, 
    			 * Net Bottle 0. 
    			 */
	    		row.getCell(3).setCellValue(brand.getDescription());
				if("BASESPIRIT".equals(brand.getBrandCategory().name())) {
	    			
	    			BaseSpiritCategory brBaseSpiritCategory = brandRepository.findById(brand.getId()).get().getBaseSpiritCategory();
					row.getCell(4).setCellValue(brBaseSpiritCategory==null?"":brBaseSpiritCategory.toString());
				}
//	    		row.getCell(5).setCellValue(0);//Size
//	    		row.getCell(6).setCellValue(0);//UOM
//	    		row.getCell(7).setCellValue(0);//Pack
	    		row.getCell(8).setCellValue(0);
    		}
    		
    		if (row.getRowNum() > 3) {
    			row.getCell(14).setCellComment(this.getCellComment(is, brand, displayUom, cocktails, row.getRowNum()));
			}
    		
    		//Jira 411
    		if(Category.GARNISH.equals(brand.getBrandCategory()) || Category.SOLIDS.equals(brand.getBrandCategory())){
    			if (!brand.hasUnitInCocktail()) {
					String formula = row.getCell(15).getCellFormula().replace("Unit", UomUtil.convertToFormulaUom(presentationUOM));
					row.getCell(15).setCellFormula(formula);
				}
    		}
    	}	
    }
    
    /*
     * Jira 414 - Header rules:
	 *	1)"(UOM selected in presentation) Cost" for Base Spirit/Modifier, Juice/Liquid and Sweetener section header.
	 *	2)Other sections always have "Portion Cost" as header, never change ;
     */
    private void setupColumnHeaders(XSSFSheet is, String presentationUOM) {
    	int baseSpiritRow = 6;
    	int juiceLiquidRow = 12;
    	int sweetenerRow = 18;
    	int costColumn = 15;
    	String headerUOM = (StringUtil.isNotEmpty(presentationUOM)?presentationUOM:"OZ") + " Cost";
    	
    	is.getRow(baseSpiritRow).getCell(costColumn).setCellValue(headerUOM);
    	is.getRow(juiceLiquidRow).getCell(costColumn).setCellValue(headerUOM);
    	is.getRow(sweetenerRow).getCell(costColumn).setCellValue(headerUOM);
	}

	private Comment getCellComment(XSSFSheet sheet, ExcelBrand brand, String displayUom, List<Cocktail> cocktails, int rowNum) {
    	String commentStr = "Bottles";
    	
    	if (StringUtil.isNotEmpty(displayUom)) {
			if (displayUom.equalsIgnoreCase("Unit")) {
				commentStr = "Unit";
			}
		} else {
			int brandPkid = brand.getId();
			Cocktail cocktail;
			String cocktailBrandUom = null;
			outerloop:
			for (int i = 0; i < cocktails.size(); i++) {
				cocktail = cocktails.get(i);
				for (Iterator<CocktailBrand> iterator = cocktail.getBrandSet().iterator(); iterator.hasNext();) {
					CocktailBrand cocktailBrand = iterator.next();
					if (brandPkid == cocktailBrand.getBrandPkId() ) {
						cocktailBrandUom = cocktailBrand.getUom();
						break outerloop;
					} else if (cocktailBrand.getBrand().getHousemadeList() != null && !cocktailBrand.getBrand().getHousemadeList().isEmpty()){
						for (Iterator<HousemadeBrand> iterator2 = cocktailBrand.getBrand().getHousemadeList().iterator(); iterator.hasNext();) {
							HousemadeBrand hmBrand = iterator2.next();
							if (brandPkid == hmBrand.getIhBrandPkId() ) {
								cocktailBrandUom = hmBrand.getIhUom();
								break outerloop;
							}
						}
					}
					
				}
			}
			if (StringUtil.isNotEmpty(cocktailBrandUom)) {
				commentStr = UomUtil.convertToFormulaUom(cocktailBrandUom);
			}
			
		}
    	
		CreationHelper factory = sheet.getWorkbook().getCreationHelper();
		ClientAnchor anchor = factory.createClientAnchor();
		anchor.setRow1(rowNum);
		anchor.setRow2(rowNum+1);
        anchor.setCol1(14);
        anchor.setCol2(15);
		Drawing<?> drawing = sheet.createDrawingPatriarch();
        Comment comment = drawing.createCellComment(anchor);
        //set the comment text and author
        comment.setString(factory.createRichTextString(commentStr));

        
		return comment;
	}

	private void createIngredientSheetRows(XSSFSheet is, Map<Integer, ExcelBrand> brandMap) {
    	int count = 0;
    	int[] brandCounts = {0, 0, 0, 0, 0};
    	List<ExcelBrand> sorted = new ArrayList<ExcelBrand>(brandMap.values());
    	Collections.sort(sorted);
    	for(ExcelBrand brand : sorted) {
    		if(brand.getBrandCategory()!=null /*&& brand.getPrices().size() > 0*/) {
    			int startRow;
    			switch (brand.getBrandCategory()){
					case BASESPIRIT:
						startRow = INGREDIENT_STARTROWS[0];
						brandCounts[0] = brandCounts[0] + 1;
						break;
					case JUICE:
						startRow = INGREDIENT_STARTROWS[1];
						brandCounts[1] = brandCounts[1] + 1;
						break;
					case SWEETENER:
						startRow = INGREDIENT_STARTROWS[2];
						brandCounts[2] = brandCounts[2] + 1;
						break;
					case SOLIDS:
						startRow = INGREDIENT_STARTROWS[3];
						brandCounts[3] = brandCounts[3] + 1;
						break; 
					case GARNISH:
						startRow = INGREDIENT_STARTROWS[4];
						brandCounts[4] = brandCounts[4] + 1;
						break;
					default:
						startRow = 0;
    			}
    			if(startRow == 0) break;
    			brand.setRow(startRow + count);
    			count++;
    		}
    	}
    	int shifted = 0;
    	for(int i=0;i<5;i++) {
    		if(brandCounts[i] > 0) {
    			
    			insertIngredientRows(is, INGREDIENT_STARTROWS[i] + shifted, brandCounts[i], i);
        		shifted += brandCounts[i];
    		}    		
    	}
    	
    	for (int i = 0; i < 5; i++) {
    		is.removeRow(is.getRow(is.getLastRowNum()));
		}

    }
    
    private void addBrandCategory(List<Cocktail> cocktails, final Map<Integer, ExcelBrand> brandMap) {
    	for(Cocktail cocktail : cocktails) {
    		for(CocktailBrand cb : cocktail.getBrandSet()) {
    			ExcelBrand brand = brandMap.get(cb.getBrandPkId());
    			if(brand == null){
    				brand = new ExcelBrand();
    				// add Housemade Ingredients
                    Ingredient ingredient = ingredientRepository.findByInBrandPkidAndInType(cb.getBrandPkId(), IngredientType.housemade);
                    if (ingredient != null) {
                    	List<HousemadeBrand> housemadeBrands = housemadeBrandRepository.findByIhHousemadeIngredientPkId(ingredient.getPkId());
                    	if (!CollectionUtils.isEmpty(housemadeBrands)) {
                    		for (HousemadeBrand housemadeBrand : housemadeBrands) {
                    			if (brandMap.get((int) housemadeBrand.getIhBrandPkId()) != null) {
                    				ExcelBrand excelBrand = (ExcelBrand)brandMap.get((int) housemadeBrand.getIhBrandPkId());
                    				excelBrand.setDescription(housemadeBrand.getInBrand().getName());
                    				excelBrand.setBrandCategory(housemadeBrand.getInCategory());
                    				brandMap.put((int) housemadeBrand.getIhBrandPkId(), excelBrand);
								} else {
									ExcelBrand brandNoPrice = new ExcelBrand();
									brandNoPrice.setId((int) housemadeBrand.getIhBrandPkId());
									brandNoPrice.setDescription(housemadeBrand.getInBrand().getName());
									brandNoPrice.setBrandCategory(housemadeBrand.getInCategory());
									brandMap.put((int) housemadeBrand.getIhBrandPkId(), brandNoPrice);
								}
                    		}
                    	}
                    	brand.setHousemadeBrands(housemadeBrands);
    				} 
    				brand.setId(cb.getBrandPkId());
                    brand.setBrandCategory(cb.getCategory());
                    brand.setDescription(cb.getBrandName());
                    brandMap.put(brand.getId(), brand);
    			} else {
    				brand.setBrandCategory(cb.getCategory());
    			}
    			brand.setDescription(cb.getBrandName());
    			brand.addUom(cb.getUom());
    		}
    	}
    }
    
    private void fillHouseBrand(XSSFSheet cs, XSSFSheet is,List<HousemadeBrand> housemadeBrands, String calUom, int cStartRow, String cName, int fsRealRow, Map<Integer, ExcelBrand> brandPrices) {
    	XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(cs);
        XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper.createFormulaListConstraint("uoms_val");
    	if (housemadeBrands != null && housemadeBrands.size() > 0) {
    		for (int h=0;h<housemadeBrands.size();h++) {
    			HousemadeBrand hb = housemadeBrands.get(h);
    			int cCodeRow = cStartRow + h;
                int cRealRow = cCodeRow + 1;
                int iCodeRow = brandPrices.get((int)(hb.getIhBrandPkId())) == null ? 0 : brandPrices.get((int)(hb.getIhBrandPkId())).getRow();
                int iRealRow = iCodeRow + 1;
                //cocktail sheet filling
                XSSFRow cBaseRow = cs.getRow(cCodeRow);
                cBaseRow.getCell(1).setCellValue(hb.getInBrand().getName());
                //no calculation on unit/solids/garnish
                String brandUom = hb.getIhUom();
                if(UOM_UNIT.equals(brandUom) /*|| StringUtil.isAllEmpty(brandUom)
                        || Category.SOLIDS.equals(hb.getInCategory()) || Category.GARNISH.equals(hb.getInCategory())*/) {
                    cBaseRow.getCell(2).setCellValue(hb.getIhQuantity());
                    cBaseRow.getCell(3).setCellValue(brandUom);
                    cBaseRow.getCell(4).setCellFormula(String.format(COCKTAIL_COST_UNIT, cRealRow, iRealRow));
                } else {
                    //portion formula to calculate on oz value/cost
//                    double ozValue = UomUtil.convertUom(hb.getIhQuantity(), brandUom, UOM_OZ);
//                    cBaseRow.getCell(2).setCellFormula(String.format(cocktailPortion, ozValue, cRealRow));
//                    cBaseRow.getCell(3).setCellValue(calUom);
//                    cBaseRow.getCell(4).setCellFormula(String.format(cocktailCostOz, cRealRow, iRealRow));
                    
    				String formulaUom = UomUtil.convertToFormulaUom(brandUom);
    				String cocktailPortionFormula = String.format(cocktailPortion, hb.getIhQuantity(), cRealRow).replace("oz", formulaUom);
    				cBaseRow.getCell(2).setCellFormula(cocktailPortionFormula);
    				cBaseRow.getCell(3).setCellValue(UomUtil.convertToFormulaUom(calUom));
    				cBaseRow.getCell(4).setCellFormula(String.format(cocktailCostOz, cRealRow, iRealRow));
                }
                //add uom  list
                System.out.println(cs.getSheetName()+" creating dropdown list on row " + cCodeRow);
                XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, new CellRangeAddressList(cCodeRow, cCodeRow, 3, 3));
                validation.setSuppressDropDownArrow(true);
                validation.setShowErrorBox(false);
                cs.addValidationData(validation);
              //ingredient sheet : fill Quantity used
                if (iCodeRow > 0) {
                	XSSFRow iBaseRow = is.getRow(iCodeRow);
        			XSSFCell cell = iBaseRow.getCell(14);
        			String reference = String.format(INGREDIENT_USED_REF, cName, cRealRow, fsRealRow);
        			if (cell.getCellTypeEnum() == CellType.BLANK) {
        				if(UOM_UNIT.equals(brandUom)) {
        					cell.setCellFormula(String.format(INGREDIENT_UNIT_USED, reference));
        				} else {
        					cell.setCellFormula(String.format(ingredientBottleUsed, reference, iRealRow));
        				}
        			} else {
        				/*
        				 * Housemade is excluded from ingredient list page so can not calcuate its cost
        				 * This is a potential defect in ProfitCalc template
        				 */
        				if (!Category.HOUSEMADE.equals(hb.getInCategory())) {
        					String formula = cell.getCellFormula();
        					int index  = formula.indexOf("))))");
        					formula = formula.substring(0, index) + "," + reference + formula.substring(index, formula.length());
        					System.out.println(formula);
        					cell.setCellFormula(formula);
        				}
        			}
    			}
    		}
		}
    }
}
