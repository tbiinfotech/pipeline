package com.liquidpresentaion.cocktailservice.util;

import static com.google.common.collect.Lists.newArrayList;
import static java.math.RoundingMode.HALF_UP;
//import static utils.UOMConverter.convert;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Closeables;
import com.itextpdf.text.log.SysoCounter;

import static com.google.common.xml.XmlEscapers.xmlAttributeEscaper;
import com.liquidpresentaion.cocktailservice.model.Cocktail;
import com.liquidpresentaion.cocktailservice.model.CocktailBrand;
import com.liquidpresentaion.cocktailservice.model.CocktailGlassStyle;
import com.liquidpresentaion.cocktailservice.model.HousemadeBrand;
import com.liquidpresentaion.cocktailservice.model.Ingredient;
import com.liquidpresentaion.cocktailservice.model.Presentation;
import com.liquidpresentaion.cocktailservice.repository.BrandRepository;
import com.liquidpresentaion.cocktailservice.repository.CocktailBrandRepository;
import com.liquidpresentaion.cocktailservice.repository.HousemadeBrandRepository;
import com.liquidpresentaion.cocktailservice.repository.IngredientRepository;
import com.liquidpresentation.common.Category;
import com.liquidpresentation.common.IngredientType;

import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRGraphics2DExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
import net.sf.jasperreports.engine.util.DeepPrintElementVisitor;
import net.sf.jasperreports.engine.util.DefaultPrintElementVisitor;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleGraphics2DExporterOutput;
import net.sf.jasperreports.export.SimpleGraphics2DReportConfiguration;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import play.libs.F.Tuple;

@Component
public class PresentationRender {
    
    private static final Logger log = LoggerFactory.getLogger(PresentationRender.class.getName());
    
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private CocktailBrandRepository cockBrandRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private HousemadeBrandRepository housemadeBrandRepository;
    
    //FIXME Move to resources
    private static final String MIXOLOGIST_NOTES_TITLE = "MIXOLOGIST NOTES";
    private static final String GARNISH_TITLE = "GARNISH";
    private static final String INGREDIENTS_TITLE = "INGREDIENTS";
    private static final String GLASSWARE_TITLE = "GLASSWARE";
    private static final String HOUSE_MADE_TITLE = "HOUSE MADE";
    private static final String METHOD_TITLE = "METHOD";
    private static final String USED_IN_TITLE = "USED IN";

    public byte[] renderPDF(Presentation p) {
		JRPdfExporter pdfExporter = new JRPdfExporter();
		SimpleDateFormat formatter = new SimpleDateFormat("MMMMM dd, yyyy", Locale.ENGLISH);
		if(p.getDate()!= null){
			p.setDateStrings(formatter.format(p.getDate()));
		}
		return renderReport(p, pdfExporter);
 	}

    public static byte[] renderPDF(PresentationExportable exp) {
        JRPdfExporter pdfExporter = new JRPdfExporter();
        return renderReport(exp, pdfExporter);
    }

    public byte[] renderPPT(Presentation p) {
		JRPptxExporter pptExporter = new JRPptxExporter();
		SimpleDateFormat formatter = new SimpleDateFormat("MMMMM dd, yyyy", Locale.ENGLISH);
		if(p.getDate()!= null){
			p.setDateStrings(formatter.format(p.getDate()));
		}
		return renderReport(p, pptExporter);
    }

    public static byte[] renderPPT(PresentationExportable exp) {
        JRPptxExporter pptExporter = new JRPptxExporter();
        return renderReport(exp, pptExporter);
    }
    
    public  byte[] renderPNG(Presentation p, Integer no) {
        Tuple<JasperPrint, Integer> jasperPrint = preparePresentation(p, true).getPage(no);
        return renderPNG(jasperPrint);
    }
    
    public static byte[] renderPNG(PresentationExportable exp, Integer no) {
        Tuple<JasperPrint, Integer> jasperPrint = exp.getPage(no);
        return renderPNG(jasperPrint);
    }
    
    public  PresentationExportable preparePresentation(Presentation p, boolean isPdf) {
        Stopwatch sw = Stopwatch.createStarted();
        Velocity.init();
        VelocityContext velocityContext = new VelocityContext();
        Map<String, Object> params = Maps.newHashMap();
        Map<String, JRBeanCollectionDataSource> dataSets = Maps.newHashMap();
        fillParams(p, velocityContext, params, dataSets);
        velocityContext.put("isPdf", isPdf);
        velocityContext.put("D", "$");

        log.trace("Velocity setup completed. Time since start: {}ms", sw.elapsed(TimeUnit.MILLISECONDS));
        try {
            List<JasperPrint> reports = Lists.newArrayList();
            reports.add(truncateEmpty(jasperPage(velocityContext, params, dataSets, "templates/cover.jrxml")));
            log.trace("Cover generated. Time since start: {}ms", sw.elapsed(TimeUnit.MILLISECONDS));
            
            if (StringUtils.isNotBlank(p.getAccountLogo())) {
            	velocityContext.put("accountLogo", p.getAccountLogo());
            }
            if (StringUtils.isNotBlank(p.getBrandedElement())) {
            	velocityContext.put("brandedElement", p.getBrandedElement());
            }
//            if (!p.getBrandedElement().isEmpty()) {
//                  velocityContext.put("currentBannerImage", p.getBrandedElement());
//                  reports.add(jasperPage(velocityContext, params, dataSets, "templates/banner.jrxml"));
//            }
//            reports.add(jasperPage(velocityContext, params, dataSets, "templates/cocktail.jrxml"));
            log.trace("Banner generated. Time since start: {}ms", sw.elapsed(TimeUnit.MILLISECONDS));

            Map<CocktailBrand, List<Tuple<String, Integer>>> hm2pages = new HashMap<CocktailBrand, List<Tuple<String, Integer>>>();
            
            for(Cocktail c : p.getCocktails()) {
                params.put("PAGE_OFFSET", getTotalPages(reports));
                if (StringUtils.isBlank(c.getImage())) {
                    c.setImage(c.getDefaultImage());
                }
                reports.add(truncateCocktail(jasperCocktail(c, p.getUom(), velocityContext, dataSets, params, hm2pages)));
            }
            log.trace("Cocktails generated. Time since start: {}ms", sw.elapsed(TimeUnit.MILLISECONDS));
//            log.trace("param println = ",params.toString());
            System.out.println("param println = {"+params.toString()+"} ");
            if (!hm2pages.isEmpty()) {
                reports.add(jasperPage(velocityContext, params, new HashMap(), "templates/appendix.housemade.cover.jrxml"));
                for (CocktailBrand mi : hm2pages.keySet()) {
                    reports.add(truncateHomemade(jasperHomemade(mi, hm2pages.get(mi), velocityContext, dataSets, params)));
                }
            }
            log.trace("HM appendix generated. Time since start: {}ms", sw.elapsed(TimeUnit.MILLISECONDS));

            return new PresentationExportable(reports);
        } catch (JRException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] renderPNG(Tuple<JasperPrint, Integer> jasperPrint) {
        try {
            JRGraphics2DExporter exporter = new JRGraphics2DExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint._1));
            BufferedImage bi = new BufferedImage(jasperPrint._1.getPageWidth()*2, jasperPrint._1.getPageHeight()*2, BufferedImage.TYPE_INT_ARGB);
            SimpleGraphics2DExporterOutput output = new SimpleGraphics2DExporterOutput();
            output.setGraphics2D(bi.createGraphics());
            exporter.setExporterOutput(output);
            SimpleGraphics2DReportConfiguration configuration = new SimpleGraphics2DReportConfiguration();
            configuration.setZoomRatio(2f);
            configuration.setPageIndex(jasperPrint._2);
            exporter.setConfiguration(configuration);
            exporter.exportReport();
            ByteArrayOutputStream outTotal = new ByteArrayOutputStream();
            ImageIO.write(bi, "PNG", outTotal);

            return outTotal.toByteArray();
        } catch (JRException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private  byte[] renderReport(Presentation p, JRAbstractExporter exporter) {
        return renderReport(preparePresentation(p, exporter instanceof JRPdfExporter), exporter);
	}

    private static byte[] renderReport(PresentationExportable exportable, JRAbstractExporter exporter) {
        try {
            final ByteArrayOutputStream outTotal = new ByteArrayOutputStream();
            export(exportable.getPrintables(), outTotal, exporter);
            return outTotal.toByteArray();
        } catch (JRException e) {
            throw new RuntimeException(e);
        }
    }

	private JasperPrint jasperCocktail(Cocktail c,String uom, VelocityContext velocityContext,  
	        Map<String, JRBeanCollectionDataSource> dataSets, Map<String, Object> params, 
	        Map<CocktailBrand, List<Tuple<String, Integer>>> hm2pages) throws JRException {
	    Stopwatch sw = Stopwatch.createStarted();
		deleteSpecialAndCommandSymbols(c);

		log.trace("Cocktails prepared for generating. Time since start: {}ms", sw.elapsed(TimeUnit.MILLISECONDS));
		
		velocityContext.put("cocktail", c);

		Map<CocktailBrand,Cocktail> i2hm = extractHomemade(c.getBrandSet());
        log.trace("HM extracted. Time since start: {}ms", sw.elapsed(TimeUnit.MILLISECONDS));
        	
        if(c.getBrandSet().size()>0) {
        	Set<CocktailBrand> set = new TreeSet<CocktailBrand>(new PersonComparator()); 
	        List<CocktailBrand> list = new ArrayList<>();
//	        for(Iterator<CocktailBrand> iterator = c.getBrandSet().iterator();iterator.hasNext();){  
//	        	System.out.println(iterator.next().getIndex()+" "); 
//	        	list.add(iterator.next());
////	        	set.add(iterator.next());	      
//	        	
//	        }
	        
	        for(CocktailBrand cocktailBrand : c.getBrandSet()) {
	        	list.add(cocktailBrand);
	        }
	        list.forEach(sets->{
	        	set.add(sets);
	        });
	        c.setBrandSet(set);
	        
        }
         

        
        
        
        
        
        
		params.put("dataSource", fillCocktailDetailsDS(c, uom));
        InputStream is = getResourceAsStream("templates/cocktail_details.jrxml");
// TODO                Play.classloader.getResourceAsStream(Play.configuration.getProperty("template.cocktail.details"));
        log.trace("Template loaded. Time since start: {}ms", sw.elapsed(TimeUnit.MILLISECONDS));
        try {
            JasperDesign jasperDesign = JRXmlLoader.load(is);
            log.trace("Design loaded. Time since start: {}ms", sw.elapsed(TimeUnit.MILLISECONDS));
            JasperReport subReport = JasperCompileManager.compileReport(jasperDesign);
            log.trace("Report compiled. Time since start: {}ms", sw.elapsed(TimeUnit.MILLISECONDS));
            params.put("subReport", subReport);
        } catch (JRException e) {
            throw new RuntimeException(e);
        } finally {
            Closeables.closeQuietly(is);
        }

        JasperPrint print = jasperPage(velocityContext, params, dataSets, "templates/cocktail.jrxml");
        log.trace("Printable generated. Time since start: {}ms", sw.elapsed(TimeUnit.MILLISECONDS));
        
        if (!i2hm.isEmpty()) {        
            mapHomemadeToPages(params, hm2pages, i2hm, print);
        }
        log.trace("HM mapped to pages. Time since start: {}ms", sw.elapsed(TimeUnit.MILLISECONDS));
        
		return print;
	}
	
	class PersonComparator implements Comparator<CocktailBrand>{  
	    @Override  
	    public int compare(CocktailBrand o1, CocktailBrand o2) {      
	        return o1.getIndex() - o2.getIndex();  
	    }      
	} 

    private JasperPrint jasperHomemade(CocktailBrand mi, 
            List<Tuple<String, Integer>> occurrences, VelocityContext velocityContext,  
            Map<String, JRBeanCollectionDataSource> dataSets, Map<String, Object> params) throws JRException {
        params.put("dataSource", fillHomemadeDetailsDS(mi, occurrences));
        InputStream is = getResourceAsStream("templates/appendix.housemade.details.jrxml");
// TODO               Play.classloader.getResourceAsStream(Play.configuration.getProperty("template.appendix.housemade.details"));
        try {
            JasperDesign jasperDesign = JRXmlLoader.load(is);
            JasperReport subReport = JasperCompileManager.compileReport(jasperDesign);
            params.put("subReport", subReport);
        } catch (JRException e) {
            throw new RuntimeException(e);
        } finally {
            Closeables.closeQuietly(is);
        }
        dataSets.put("templates/appendix.housemade.jrxml", new JRBeanCollectionDataSource(newArrayList(mi)));
        JasperPrint print = jasperPage(velocityContext, params, dataSets, "templates/appendix.housemade.jrxml");
        
        return print;
    }

    private static void mapHomemadeToPages(final Map<String, Object> params, 
            final Map<CocktailBrand, List<Tuple<String, Integer>>> hm2pages,
            final Map<CocktailBrand,Cocktail> i2hm, JasperPrint print) {
        for (int idx = 0; idx < print.getPages().size(); idx++) {
            final int pageNum = idx + 1;
            JRPrintPage page = print.getPages().get(idx);
   
            for (JRPrintElement elem : page.getElements()) {
                elem.accept(new DeepPrintElementVisitor(new DefaultPrintElementVisitor() {
                    @Override
                    public void visit(JRPrintText textElement, Object arg) {
                        if (CocktailReportFields.DETAILS_CONTENT.equals(textElement.getUUID().toString())) {
                        	
                            for (CocktailBrand i : i2hm.keySet()) {
                                if (textElement.getFullText().contains(xmlAttributeEscaper().escape(i.getBrandName()))) {
                                    if (hm2pages.get(i2hm.get(i)) == null) {
                                    	List<String> nameList = hm2pages.keySet().stream().map(cocktailBrand -> cocktailBrand.getBrandName()).collect(Collectors.toList());
                                    	if(!nameList.contains(i.getBrandName())) {
                                    		hm2pages.put(i, new ArrayList<Tuple<String, Integer>>());
                                    		hm2pages.get(i).add(new Tuple(i.getCocktail().getName(), (Integer)params.get("PAGE_OFFSET") + pageNum));
                                    	}else {
                                    		
                                    		for (CocktailBrand ii : hm2pages.keySet()) {
                                    			if(i.getBrandName().equals(ii.getBrandName())) {
                                    				hm2pages.get(ii).add(new Tuple(i.getCocktail().getName(), (Integer)params.get("PAGE_OFFSET") + pageNum));
                                    			}
                                    		}
                                    	}
                                    }
                                    
                                }
                            }
                            
                            
                        }
                    }
                }), null);
            }
        }
    }

	private  Map<CocktailBrand,Cocktail> extractHomemade(Set<CocktailBrand> CocktailBrands) {
        Map<CocktailBrand,Cocktail> result = new HashMap();
        for (CocktailBrand i : CocktailBrands) {
//            if (i.type != Housemade || i.mappingIngredient == null) {
//                continue;
//            }
        	if (i.getCategory().equals(Category.HOUSEMADE)) {
        		result.put(i,i.getCocktail());
        	}
        }
        return result;
    }
    
	private static List<ReportDetail> fillCocktailDetailsDS(Cocktail c, String uom) {
	    List<ReportDetail> details = newArrayList();
	    
	    if (c.getMethod() != null && !c.getMethod().isEmpty()) {
	        details.add(new ReportDetail(METHOD_TITLE, fixStringForReport(c.getMethod())+"\n"));
	    }
	    if (c.getGlassStypleSet() != null && !c.getGlassStypleSet().isEmpty()) {
	    	List<String> glassString = new ArrayList<>();
	    	c.getGlassStypleSet().forEach(g->{
	    		glassString.add(g.getGlassStyle().toString());
	    	});
	        String glassBase = fixStringForReport(glassString.toString());
	        List<String> list = CollectionUtils.arrayToList(glassBase.split(","));
	        if (list.size() == 3) {
	        	glassBase = list.get(0) + "," + list.get(1) + " or " + list.get(2);
	        }
            details.add(new ReportDetail(GLASSWARE_TITLE, glassBase
                    .substring(1, glassBase.length()-1).replaceAll("(.)([A-Z])", "$1 $2")));
	    }

	    if (c.getBrandSet() != null && !c.getBrandSet().isEmpty()) {
	        String ingrs = buildIngredientsString(c.getBrandSet());
            details.add(new ReportDetail(INGREDIENTS_TITLE, ingrs));
	    }
	    
	    if (c.getGarnish() != null && !c.getGarnish().isEmpty()) {
	        details.add(new ReportDetail(GARNISH_TITLE, fixStringForReport(c.getGarnish())));
	    } else {
	    	details.add(new ReportDetail(GARNISH_TITLE, "None"));
	    }
	    
	    if (c.getComments() != null && !c.getComments().isEmpty()) {
	        details.add(new ReportDetail(MIXOLOGIST_NOTES_TITLE, fixStringForReport(c.getComments())));
	    }
        return details;
    }

    private Object fillHomemadeDetailsDS(CocktailBrand mi, List<Tuple<String, Integer>> occurrences) {
        List<ReportDetail> details = newArrayList();
        Set<String> processed = Sets.newHashSet();
        Ingredient ingredient = null;
        if (occurrences != null && !occurrences.isEmpty()) {
            StringBuffer sb = new StringBuffer();
            for (Tuple<String, Integer> occ : occurrences) {
                if (processed.contains(occ._1)) {
                    continue;
                }
                sb.append(occ._1).append(" (p ").append(occ._2-1).append(")").append("\n");
                processed.add(occ._1);
            }
            details.add(new ReportDetail(USED_IN_TITLE, sb.toString()));
        }
        if (mi.getCategory().equals(Category.HOUSEMADE)) {
        	ingredient = ingredientRepository.findByInBrandPkid(mi.getBrandPkId());
        	details.add(new ReportDetail(METHOD_TITLE, ingredient.getInMethod()));
        }
        if (mi != null ) {
        	if (ingredient != null) {
        		details.add(new ReportDetail(INGREDIENTS_TITLE, buildIngredientsHousemadeString(ingredient.getPkId())));
        	}
        }

        return details;
    }

	private static String buildIngredientsString(Set<CocktailBrand> brandSet) {
	    StringBuilder sb = new StringBuilder();
        for (CocktailBrand c : brandSet) {
        	if (c.getCategory().equals(Category.GARNISH)) {	// should not include garnishes into view
        		continue;
        	}
        	
            BigDecimal qty = zeroIfNull(c.getQuantity());
                    sb.append(qty.setScale(2, HALF_UP).stripTrailingZeros().toPlainString());
                    sb.append(" ");
                    sb.append(c.getUom());
                    sb.append(" ");
                    sb.append(c.getBrandName());
//                    .stripTrailingZeros().toPlainString());
//                    appendUOM(uom, sb);
            if (c.getCategory().equals(Category.HOUSEMADE)) {
                    sb.append(" ").append("<b>HM</b>");
               }
            sb.append("<br>");
        }
        
        return sb.toString().trim();
    }
	
	private String buildIngredientsHousemadeString(Long ihHousemadeIngredientPkId) {
	    StringBuilder sb = new StringBuilder();
	    List<HousemadeBrand> housemadeBrands = housemadeBrandRepository.findByIhHousemadeIngredientPkId(ihHousemadeIngredientPkId);
	    Collections.sort(housemadeBrands, new Comparator(){   
            public int compare(HousemadeBrand o1, HousemadeBrand o2) {   
                if(o1.getIndex() > o2.getIndex()){   
                    return 1;   
                }   
                if(o1.getIndex() == o1.getIndex()){   
                    return 0;   
               }   
               return -1;   
            }

			@Override
			public int compare(Object o1, Object o2) {
				// TODO Auto-generated method stub
				return 0;
			}   
        });
        for (HousemadeBrand h : housemadeBrands) {
        	if (h.getInCategory().equals(Category.GARNISH)) {	// should not include garnishes into view
        		continue;
        	}
        	Long brandPkId = h.getIhBrandPkId();
            BigDecimal qty = zeroIfNull(h.getIhQuantity());
                    sb.append(qty.setScale(2, HALF_UP).stripTrailingZeros().toPlainString());
                    sb.append(" ");
                    sb.append(h.getIhUom());
                    sb.append(" ");
                    sb.append(brandRepository.findById(brandPkId.intValue()).get().getName());
//                    .stripTrailingZeros().toPlainString());
//                    appendUOM(uom, sb);
            sb.append("<br>");
        }
        
        return sb.toString().trim();
    }

    private static void appendUOM(String uom, StringBuilder sb) {
        if (uom.isEmpty()) {
            sb.append(" ");
        } else {
            sb.append(" ").append(uom).append(" ");
        }
    }

    private static Object getTotalPages(List<JasperPrint> reports) {
        int pages = 0;
        
        for (JasperPrint p : reports) {
            pages += p.getPages().size();
        }
        
        return pages;
    }

	private static void deleteSpecialAndCommandSymbols(Cocktail c) {
    	if (c.getName() != null && !c.getName().isEmpty())
    		c.setName(fixStringForReport(c.getName()));
    	if (c.getSupplierName() != null && !c.getSupplierName().isEmpty())
    		c.setSupplierName(fixStringForReport(c.getSupplierName()));
    	if (c.getBrandName() != null && !c.getBrandName().isEmpty())
    		c.setBrandName(fixStringForReport(c.getBrandName()));
    	if (c.getGarnish() != null && !c.getGarnish().isEmpty())
    		c.setGarnish(fixStringForReport(c.getGarnish()));
//    	if (c.get != null && !c.houseMadeIngredients.isEmpty())
//			c.houseMadeIngredients = fixStringForReport(c.houseMadeIngredients);
		if (c.getComments() != null && !c.getComments().isEmpty())
			c.setComments(fixStringForReport(c.getComments()));
		if (c.getMethod() != null && !c.getMethod().isEmpty())
			c.setMethod(fixStringForReport(c.getMethod()));
		
//		for (Ingredient i : c.ingredients) {
//		    i.name = fixStringForReport(i.name);
//		}
    }
    
    private static String fixStringForReport(String string) {
        if (string == null || string.isEmpty()) {
            return "";
        }
        
    	string = string.replace('\n', ' ');	// for jasper to be happy.
    	string = string.replaceAll("\"", "\''");	// jasper don't like double quotes
    	string = string.replaceAll("[\u0000-\u001f]", "");	// replacing all command symbols with empty string.
    	return string;
    }

	private static void export(List<JasperPrint> reports,
			final OutputStream outSummary, JRAbstractExporter exporter) throws JRException {
		exporter.setExporterInput(SimpleExporterInput.getInstance(reports));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outSummary));
		exporter.exportReport();
	}

	private static void fillParams(Presentation p, VelocityContext velocityContext, Map<String, Object> params, Map<String, JRBeanCollectionDataSource> dataSets) {
        // Use Velocity to replace placeholders in template with desired values
        velocityContext.put("presentation", p);
		velocityContext.put("imageReader", new ImageReader(params));
    }

    private static JasperPrint jasperPage(VelocityContext velocityContext,
			Map<String, Object> params, Map<String, JRBeanCollectionDataSource> dataSets, String page) throws JRException {
        log.trace("Render. Page: {}\nContext: {}\nParams: {}", new Object[] {page, velocityContext, params});
		InputStream is = getResourceAsStream(page);
// TODO		        Play.classloader.getResourceAsStream(page);
	    try {
	        StringWriter writer = new StringWriter();
			Velocity.evaluate(velocityContext, writer, "", new InputStreamReader(is));
			params = Maps.newHashMap(params);
			JasperDesign jasperDesign = JRXmlLoader.load(new ByteArrayInputStream(writer.toString().getBytes()));
			for (Entry<String, Object> entry : params.entrySet()) {
				if (jasperDesign.getParametersMap().containsKey(entry.getKey())) {
					continue;
				}
				JRDesignParameter parameter = new JRDesignParameter();
				parameter.setName(entry.getKey());
				parameter.setValueClass(entry.getValue().getClass());
				jasperDesign.addParameter(parameter);
			}
			JRBeanCollectionDataSource dataSource = dataSets.get(page);
			log.trace("Page: {}. Records: {}", page, dataSource == null ? "NULL SOURCE": dataSource.getRecordCount());
			JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
			JasperPrint jasperPrint = JasperFillManager.fillReport(
			        jasperReport, params, dataSource == null ? new JREmptyDataSource() : dataSource);
            log.trace("Pages: {}", jasperPrint.getPages().size());
			return jasperPrint;
        } catch (JRException e) {
            throw new RuntimeException(e);
		} finally {
            Closeables.closeQuietly(is);
        }
	}

    private static JasperPrint truncateEmpty(JasperPrint jasperPrint) {
        List<JRPrintPage> pages = jasperPrint.getPages(); // this is hack to get rid of empty pages, for some reason they appears after adding "\n" in the report.
        for (Iterator<JRPrintPage> i=pages.iterator(); i.hasNext();) {
        	JRPrintPage currentPage = (JRPrintPage) i.next();
        	
        	if (currentPage.getElements().size() < 5)
        		i.remove();
        }
        
        return jasperPrint;
    }

    private static JasperPrint truncateCocktail(JasperPrint jasperPrint) {
        log.trace("Truncating cocktail");
        List<JRPrintPage> pages = jasperPrint.getPages();
        int pageNum = 1;
        for (Iterator<JRPrintPage> i = pages.iterator(); i.hasNext();) {
            final JRPrintPage currentPage = (JRPrintPage) i.next();
            log.trace("Page: {} [{}]", currentPage, pageNum++);
            List<Boolean> hasContent = newArrayList(Boolean.FALSE);
            for (final JRPrintElement elem : currentPage.getElements()) {
                elem.accept(new DeepPrintElementVisitor(new DefaultPrintElementVisitor() {

                    @Override
                    public void visit(final JRPrintFrame frame, Object arg) {
                        log.trace("Frame: {}", frame.getUUID().toString());
                        if (CocktailReportFields.DETAILS_FRAME.equals(elem.getUUID().toString()) 
                                && ((JRPrintFrame) elem).getElements().size() > 2) {
                            for (JRPrintElement e : ((JRPrintFrame) elem).getElements()) {
                                log.trace("Details subelement: {}", e.getUUID().toString());
                            }
                            ((List) arg).add(0, true);

                            List<JRPrintText> texts = new ArrayList();
                            frame.accept(new DeepPrintElementVisitor(new DefaultPrintElementVisitor() {
                                public void visit(JRPrintFrame frame, Object arg) {
                                    frame.accept(new DeepPrintElementVisitor(new DefaultPrintElementVisitor() {

                                        @Override
                                        public void visit(JRPrintText textElement, Object arg) {
                                            log.trace("Frame text: {}", textElement.getUUID().toString());
                                            if (CocktailReportFields.DETAILS_TITLE.equals(textElement.getUUID().toString())
                                                    || CocktailReportFields.DETAILS_CONTENT.equals(textElement.getUUID().toString())) {
                                                ((List) arg).add(textElement);
                                            }
                                        }
                                    }), arg);
                                }
                            }), texts);
                            if (texts.size() < 2 && !texts.isEmpty()) {
                                frame.getElements().remove(texts.get(0));
                            }
                        }
                    }
                }), hasContent);
            }
            
            if (!hasContent.get(0)) {
                log.trace("No content on {}", currentPage);
                i.remove();
            }
        }
        
        return jasperPrint;
    }

    private static JasperPrint truncateHomemade(JasperPrint jasperPrint) {
        List<JRPrintPage> pages = jasperPrint.getPages();

        for (final Iterator<JRPrintPage> i = pages.iterator(); i.hasNext();) {
            JRPrintPage page = i.next();
            final List<Boolean> hasContent = newArrayList(Boolean.FALSE);
            for (JRPrintElement elem : page.getElements()) {
                elem.accept(new DeepPrintElementVisitor(new DefaultPrintElementVisitor() {
                    @Override
                    public void visit(JRPrintFrame frame, Object arg) {
                        for (JRPrintElement elem : frame.getElements()) {
//                            if (CocktailReportFields.HOMEMADE_SUBREPORT.equals(elem.getUUID().toString())) {
                        	if (CocktailReportFields.HOMEMADE_CONTENT.equals(elem.getUUID().toString())) {
                                hasContent.add(0, true);
                            }
                        }
                    }
                }), null);
            }
            
            if (!hasContent.get(0)) {
                log.trace("No content on {}", page);
                i.remove();
            }
        }
        
        return jasperPrint;
    }
    
    private static BigDecimal zeroIfNull(double val) {
        return val == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(val);
    }
    
    public static class ImageReader {
    	private Map<String, Object> parameters;

		public ImageReader(Map<String,Object> parameters) {
    		this.parameters = parameters;
    	}

        public String read(String fileName) {
            InputStream is = getResourceAsStream(fileName);
// TODO                    Play.classloader.getResourceAsStream(fileName);
        	return getParameterName(is);
        }

		private String getParameterName(InputStream is) {
			if (is == null) {
				return "";
			}
			String paramName = UUID.randomUUID().toString();
        	parameters.put(paramName, is);
            return "$P{" + paramName + "}";
		}
		public String blob(String urlString) {
			//urlString = "https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1536212767&di=bbf90a83934da18bff525efc107697b7&src=http://pic35.photophoto.cn/20150620/0042040529243596_b.jpg";
			if (urlString == null) {
            	return "";
            }
			URL url = null;
			URLConnection conn  = null;
			InputStream is = null;
			InputStream in = null;
			try {
				url = new URL(urlString);
				conn = url.openConnection();
				conn.setConnectTimeout(4000);
				is = conn.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
	            byte[] buffer = new byte[1024];
	            int num = is.read(buffer);
	            while (num != -1) {
	                baos.write(buffer, 0, num);
	                num = is.read(buffer);
	            }
	            baos.flush();
	            in = new ByteArrayInputStream(baos.toByteArray());
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
        	return getParameterName(in);
		}
//        public String blob(Blob blob) {
//            if (blob == null) {
//            	return "";
//            }
//        	InputStream is = blob.get();
//        	return getParameterName(is);
//        }
    }
    
    public static class PresentationExportable {

        private List<JasperPrint> elements = newArrayList();
        
        public PresentationExportable(List<JasperPrint> reports) {
            this.elements = reports;
        }

        public Tuple<JasperPrint, Integer> getPage(Integer pageNum) {
            int count = 0;
            for (JasperPrint p : elements) {
                int pageCount = 0;
                for (JRPrintPage page : p.getPages()) {
                    if (count++ == pageNum) {
                        return new Tuple<JasperPrint, Integer>(p, pageCount);
                    }
                    pageCount++;
                }
            }
            
            return null;
        }
        
        public int getPageCount() {
            if (elements == null || elements.isEmpty()) {
                return 0;
            }
            
            int count = 0;
            for (JasperPrint p : elements) {
                count += p.getPages().size();
            }
            
            return count;
        }
        
        public List<JasperPrint> getPrintables() {
            return elements;
        }
    }

    public static class ReportDetail {
        private String title;
        private String content;
        
        public ReportDetail(String title, String content) {
            this.title = title;
            this.content = content;
        }
        
        public String getTitle() {
            return title;
        }
        public String getContent() {
            return content;
        }
        
        @Override
        public String toString() {
            return "\n" + title + " | " + content + "\n";
        }
    }
    
    interface CocktailReportFields {
        public String DETAILS_FRAME = "9ddd1dbb-78d9-4694-a7d8-2c0cb25de8a0";
        public String METHOD_TITLE = "f76f1186-64f9-4d19-855f-effbeadbed0a";
        public String METHOD_CONTENT = "a4a53dc6-314c-46e2-b3dd-002179ce2473";
        public String HOUSE_MADE_TITLE = "051ad1b6-0716-4903-a5b9-22357096c59c";
        public String HOUSE_MADE_CONTENT = "252d1454-556a-4576-8285-74a8a64f04e3";
        public String GLASSWARE_TITLE = "79d2364c-0a0b-4a44-b2a2-dc6f4e07ce11";
        public String GLASSWARE_CONTENT = "97772e99-00a4-43ea-b65c-3c22fa3e3fad";
        public String INGREDIENTS_TITLE = "81193a2a-8121-4ed2-bf88-36ef0f17a36b";
        public String INGREDIENTS_CONTENT = "85c0102e-acb2-4644-a9d2-3c0aff60c1c7";
        public String GARNISH_TITLE = "4848323d-f6bf-4142-a6f4-e9c12515ad04";
        public String GARNISH_CONTENT = "b996c8cf-c5ee-4b93-b7dc-185e5b5617b4";
        public String MIXOLOGIST_TITLE = "d039dbfd-d2c5-47ca-84ae-903e43982695";
        public String MIXOLOGIST_CONTENT = "c6112401-efd2-46c4-9322-15bbe84b091e";
        public String DEGREE_TITLE = "aab902c2-3f65-4fb9-abdc-e93a905d7afd";
        public String DETAILS_TITLE = "71438e7a-c049-11e6-a4a6-cec0c932ce01";
        public String DETAILS_CONTENT = "819301c0-c049-11e6-a4a6-cec0c932ce01";
        public String HOMEMADE_CONTENT = "592c2ee0-6857-11e6-8b77-86f30ca893d3";
        public String HOMEMADE_SUBREPORT = "a46e8f3e-6865-11e6-8b77-86f30ca893d3";
    }
    
    private static final Map<String, byte[]> prop2resource = Maps.newHashMap();
    
    private static InputStream getResourceAsStream(String name) {
    	InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(name);
        
//            prop2resource.put(name, Streams.asString(is).getBytes(StandardCharsets.UTF_8));
		return inputStream;
        
    }
}
