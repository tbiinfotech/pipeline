package com.liquidpresentaion.cocktailservice.services;

import static com.liquidpresentation.common.utils.StringUtil.isNotEmpty;
import static com.liquidpresentation.common.utils.StringUtil.toDate;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.liquidpresentaion.cocktailservice.context.UserContextHolder;
import com.liquidpresentaion.cocktailservice.model.Cocktail;
import com.liquidpresentaion.cocktailservice.model.ExcelBrand;
import com.liquidpresentaion.cocktailservice.model.Group;
import com.liquidpresentaion.cocktailservice.model.GroupUser;
import com.liquidpresentaion.cocktailservice.model.Presentation;
import com.liquidpresentaion.cocktailservice.model.Price;
import com.liquidpresentaion.cocktailservice.repository.CocktailRepository;
import com.liquidpresentaion.cocktailservice.repository.GroupRepository;
import com.liquidpresentaion.cocktailservice.repository.GroupUserRepository;
import com.liquidpresentaion.cocktailservice.repository.PresentationRepository;
import com.liquidpresentaion.cocktailservice.repository.PriceRepository;
import com.liquidpresentaion.cocktailservice.specification.SearchSpecification;
import com.liquidpresentaion.cocktailservice.util.PresentationRender;
import com.liquidpresentaion.cocktailservice.util.ProfitCalGeneratorUtil;
import com.liquidpresentation.common.ExcelData;
import com.liquidpresentation.common.exceptions.DuplicateEntityException;
import com.liquidpresentation.common.utils.ExportExcelUtils;
import com.lowagie.text.DocumentException;

@Service
public class PresentationService {

	@Autowired
	private PresentationRepository presentationRepository;

	@Autowired
	private ProfitCalGeneratorUtil profitCalGeneratorUtil;
	
	@Autowired
	private PresentationRender presentationRender;
	
	@Autowired
	private AmazonClient amazonClient;
	
	@Autowired
	private GroupUserRepository groupUserRepository;
	
	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private PriceRepository priceRepository;
	
	@Autowired
	private CocktailRepository cocktailRepository;
	
	public Page<Presentation> findAll(PageRequest pageRequest) {
		if (UserContextHolder.getContext().isAdmin()) {
			return presentationRepository.findAll(pageRequest);
		} else {
			return presentationRepository.findSelfPresentationByNativeQuery(null, null, null, pageRequest, UserContextHolder.getContext().getUserId().intValue());
		}
	}

	public void savePresentation(Presentation newPresentation) {
		validateDuplicate(newPresentation.getTitle());
		presentationRepository.save(newPresentation);
	}

	public void updatePresentation(Presentation presentation) {
		validateDuplicate(presentation.getTitle(), presentation.getId());
		Long userId = UserContextHolder.getContext().getUserId();
		presentation.setCreatePkId(userId.intValue());
		presentationRepository.save(presentation);
	}

	public void deletePresentation(Presentation presentation) {
		presentationRepository.delete(presentation);
	}

	public Presentation getPresentation(int presentationId) {
		return presentationRepository.findById(presentationId).get();
	}

	public Page<Presentation> findByKeyword(String keyword, String startDate, String endDate, PageRequest pageRequest) {
		Page<Presentation> retsultsPage;
		if (isNotEmpty(keyword, startDate, endDate)) {
			retsultsPage = presentationRepository.findByDateBetweenAndTitleIgnoreCaseContaining(toDate(startDate),
					toDate(endDate), keyword, pageRequest);
		} else if (isNotEmpty(keyword, startDate)) {
			retsultsPage = presentationRepository.findByDateAfterAndTitleIgnoreCaseContaining(toDate(startDate),
					keyword, pageRequest);
		} else if (isNotEmpty(keyword, endDate)) {
			retsultsPage = presentationRepository.findByDateBeforeAndTitleIgnoreCaseContaining(toDate(endDate), keyword,
					pageRequest);
		} else if (isNotEmpty(startDate, endDate)) {
			retsultsPage = presentationRepository.findByDateBetween(toDate(startDate), toDate(endDate), pageRequest);
		} else if (isNotEmpty(startDate)) {
			retsultsPage = presentationRepository.findByDateAfter(toDate(startDate), pageRequest);
		} else if (isNotEmpty(endDate)) {
			retsultsPage = presentationRepository.findByDateBefore(toDate(endDate), pageRequest);
		} else {
			retsultsPage = presentationRepository.findByTitleIgnoreCaseContaining(keyword, pageRequest);
		}
		return retsultsPage;
	}

	public void duplicatePresentation(int sourceId, Presentation target) {
		validateDuplicate(target.getTitle());
		Optional<Presentation> optional = presentationRepository.findById(sourceId);
		if (optional.isPresent()) {
			Presentation source = optional.get();
			BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
			Cocktail tc;
			for (Cocktail sc : source.getCocktails()) {
				tc = new Cocktail();
				tc.setId(sc.getId());
				target.addCocktail(tc);
			}
			presentationRepository.save(target);
		}
	}

	public static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        String[] initializeArray =  {"id", "customerAccountId", "customerAcctName", "title", "accountLogo", "archived", "cocktails", "createPkId", "createDate", "updatePkId", "updateDate"};
        for(java.beans.PropertyDescriptor pd : pds) {
        	Object srcValue;
        	if(!ArrayUtils.contains(initializeArray, pd.getName())) {
        		srcValue = src.getPropertyValue(pd.getName());
        		if (srcValue == null) emptyNames.add(pd.getName());
        	}
        }
        emptyNames.addAll(Arrays.asList(initializeArray));
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
	
	public long getNumberOfPresentations(int userId) {
		return presentationRepository.countByCreatePkId(userId);
	}

	public ByteArrayInputStream generatePdf(int presentationId, Map<String, String> map) throws IOException, DocumentException {
		Optional<Presentation> opt = this.presentationRepository.findById(presentationId);
		if (opt.isPresent()) {
			Presentation p = opt.get();
			/*Map<String, Object> data = new HashMap<>();
			data.put("title", p.getTitle());
			data.put("cocktails", p.getCocktails());
			return this.pdfGenaratorUtil.createPdf("PresentationTpl", data);*/
//			return this.pdfGenaratorUtil.createPdf("presentations_pdf", p);
			map.put("title", p.getTitle());
			return new ByteArrayInputStream(this.presentationRender.renderPDF(p));
		} else {
			throw new FileNotFoundException("Presentaion [" + presentationId + "] not found!");
		}
	}

	public ByteArrayInputStream generatePdf(Presentation presentation) throws IOException, DocumentException {
		presentation.setCocktails(cocktailRepository.findByIdIn(presentation.getCocktails().stream().map(Cocktail::getId).collect(Collectors.toList())));
		return new ByteArrayInputStream(this.presentationRender.renderPDF(presentation));
//		return this.pdfGenaratorUtil.createPdf("presentations_pdf", presentation);
	}

	public ByteArrayInputStream generatePptx(int presentationId, Map<String, String> map) throws Exception {
		Optional<Presentation> opt = this.presentationRepository.findById(presentationId);
		if (opt.isPresent()) {
			Presentation p = opt.get();
			map.put("title", p.getTitle());
//			return this.pptxGenaratorUtil.createPptx(p);
			return new ByteArrayInputStream(this.presentationRender.renderPPT(p));
		} else {
			throw new FileNotFoundException("Presentaion [" + presentationId + "] not found!");
		}	
	}


	public List<String> convertPdfToImages(ByteArrayInputStream pdfStream) {
		List<String> imageList = new ArrayList<>();
		try {
			PDDocument document = PDDocument.load(pdfStream);
			@SuppressWarnings("unchecked")
			List<PDPage> list = document.getDocumentCatalog().getAllPages();
			String fileNamePrefix = String.valueOf(System.currentTimeMillis());
			int pageNumber = 1;
			for (PDPage page : list) {
				String fileName = fileNamePrefix + "_" + pageNumber + ".png";
				
				BufferedImage image = page.convertToImage();
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				ImageIO.write(image, "png", os);
				byte[] inputBytes = os.toByteArray();
				InputStream is = new ByteArrayInputStream(inputBytes);
				imageList.add(amazonClient.uploadImageInputStream(fileName, is, inputBytes.length));
				
				pageNumber++;
			}
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return imageList;
	}

	public List<String> viewPresentation(int presentationId) throws IOException, DocumentException {
		return this.convertPdfToImages(this.generatePdf(presentationId, new HashMap<>()));
	}

	public List<String> viewPresentation(Presentation presentation) throws IOException, DocumentException {
		presentation.setCocktails(cocktailRepository.findByIdIn(presentation.getCocktails().stream().map(Cocktail::getId).collect(Collectors.toList())));
		return this.convertPdfToImages(this.generatePdf(presentation));
	}
	
	public List<Presentation> findPresentationTeamNativeQuery(String startDate, String endDate, Integer salesGroupId) {
		return presentationRepository.findPresentationTeamNativeQuery(toDate(startDate), toDate(endDate), salesGroupId);
	}
	
	public List<Presentation> findByCreateDateBetweenAndCreatePkIdIsOrderByCustomerAcctName(String startDate, String endDate, Integer createPkId) {
		return presentationRepository.findByCreateDateBetweenAndCreatePkIdIsOrderByCustomerAcctName(toDate(startDate), toDate(endDate), createPkId);
	}
	
	public List<Presentation> findByCreateDateBetweenAndCreatePkIdInOrderByCustomerAcctName(String startDate, String endDate, Integer salesGroupId) {
		Group group = new Group();
		group.setPkId(salesGroupId);
		List<GroupUser> list = new ArrayList<>();
		List<Integer> createPkIdList = new ArrayList<>();
		List<Group> groups = groupRepository.findGroupNativeQuery(salesGroupId);
		list = groupUserRepository.findByGroupIn(groups);
		list.forEach(g->{createPkIdList.add(g.getUser().getPkId());}); 
		return presentationRepository.findByCreateDateBetweenAndCreatePkIdInOrderByCustomerAcctName(toDate(startDate), toDate(endDate), createPkIdList);
	}

	public ByteArrayInputStream generateProfitCal(int presentationId) throws IOException {
		Optional<Presentation> opt = this.presentationRepository.findById(presentationId);
		if (opt.isPresent()) {
			Presentation p = opt.get();
			return this.profitCalGeneratorUtil.createProfitCal("ProfitCalculatorTpl", p, this.findBrandPrices(presentationId));
		} else {
			throw new FileNotFoundException("Presentaion [" + presentationId + "] not found!");
		}
	}
	
	private void validateDuplicate(String title){
		if (presentationRepository.existsByTitleIgnoreCase(title)) {
			throw new DuplicateEntityException("There already is a presentation named '" + title + "'!");
		}
	}
	
	private void validateDuplicate(String title, int id){
		if (presentationRepository.existsByTitleIgnoreCaseAndIdIsNot(title, id)) {
			throw new DuplicateEntityException("There already is a presentation named '" + title + "'!");
		}
	}
	
	public void exportCustomers(HttpServletResponse response,List<Presentation> list) throws Exception {
		ExcelData data = new ExcelData();
		List<String> titles = new ArrayList<String>();
		titles.add("Customer Account Name");
		titles.add("Customer Account ID");
		data.setTitles(titles);
		List<List<Object>> rows = new ArrayList<List<Object>>();
		for (Presentation presentation : list) {
			List<Object> obj = new ArrayList<Object>();
			obj.add(presentation.getCustomerAcctName());
			obj.add(presentation.getCustomerAccountId());
			rows.add(obj);
		}
		data.setRows(rows);
		ExportExcelUtils.exportExcel(response,"name1",data);
	}

	public Page<Presentation> findPresentations(String keyword, String startDate, String endDate,
			PageRequest pageRequest) {
		if (UserContextHolder.getContext().isAdmin()) {
			return presentationRepository.findAll(SearchSpecification.presentationsSearch(startDate,endDate, keyword,null, null), pageRequest);
		} else {
			return presentationRepository.findAll(SearchSpecification.presentationsSearch(startDate,endDate, keyword,UserContextHolder.getContext().getUserId().intValue(), null), pageRequest);
		}
	}
	
	public Map<Integer, ExcelBrand> findBrandPrices(Integer presentationPkid){
		Map<Integer, ExcelBrand> brands = new HashMap<Integer, ExcelBrand>();
		List<Price> prices = this.priceRepository.findByPresentationPkid(presentationPkid);
		
		if (!UserContextHolder.getContext().isAdmin()) {
			List<Integer> groupPkids = UserContextHolder.getContext().getGroupPkidList();
			List<BigInteger> upperGroups = groupRepository.findUpperSalesGroupsNativeQuery(groupPkids);
			
			Price p;
			boolean hasAccess = false;
			for (Iterator<Price> iterator = prices.iterator(); iterator.hasNext();) {
				p = iterator.next();
				hasAccess = false;
				for (BigInteger groupPkid : upperGroups) {
					if (groupPkid.longValue() == p.getPrSalesGroupPkid().longValue()) {
						hasAccess = true;
					}
				}
				if (!hasAccess) {
					iterator.remove();
				}
			}
		}
		
		ExcelBrand prevBrand = null;
		for (Price price : prices) {
			if (prevBrand != null && prevBrand.getId() == price.getBrandPkid()) {
				prevBrand.addPrice(price);
				if(prevBrand.getBrandCategory()==null) {
					prevBrand.setBrandCategory(price.getPrCategory());
				}
			} else {
				prevBrand = new ExcelBrand(price.getBrandPkid().intValue(), price);
				brands.put(prevBrand.getId(), prevBrand);
			}
		}
		
		return brands;
	}
}