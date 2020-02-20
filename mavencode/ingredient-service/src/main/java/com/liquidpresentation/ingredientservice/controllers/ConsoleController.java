package com.liquidpresentation.ingredientservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.liquidpresentation.common.utils.StringUtil;
import com.liquidpresentation.ingredientservice.services.BrandService;
import com.liquidpresentation.ingredientservice.services.IngredientService;
import com.liquidpresentation.ingredientservice.services.PriceService;

@Controller
@RequestMapping(value = "console")
public class ConsoleController {
	private String encodeKouling = "$2a$12$x9oMaWIod2I27gjncu7c3e9LpUO0AyA0W1yJUxcacOY2gwZJwPQz6";
	
	@Autowired
	private PriceService priceService;
	
	@Autowired
	private IngredientService ingredientService;
	
	@Autowired
	private BrandService brandService;
	
	private void kouLing(String rawKouling){
		PasswordEncoder encoder = new BCryptPasswordEncoder(12);
		if (!encoder.matches(rawKouling, encodeKouling)) {
			throw new RuntimeException("Kou Ling Cuo Wu!");
		}
	}
	
	@RequestMapping(value = "/uploads", method = RequestMethod.GET)
	public ModelAndView findUploadMatch(@RequestParam(name = "kouling", defaultValue = "")String rawKouling, 
										@RequestParam(name = "uploadString", defaultValue = "")String uploadString){
		
		ModelAndView modelView = new ModelAndView("Uploads");
		if (StringUtil.isAllEmpty(uploadString)) {
			modelView.addObject("error", "Parameter [uploadString] required!");
		}
		try {
			this.kouLing(rawKouling);
			
			String[] prop = uploadString.split(",");
			if (prop.length < 16) {
				modelView.addObject("error", "Invalid Parameter [uploadString]!");
			} else {
				String distributorId = prop[4];
				String itemCode = prop[5];
				String salesGroupId = prop[6];
				String state = prop[7];
				String ingredientId = prop[0];
				String distributorBrandId = prop[15];
				String mpc = prop[1];
				
				
				modelView.addObject("matchAC1", priceService.findByPrDistributorPkidAndPrDistribtorItemCodeAndPrSalesGroupPkidOrPrState(Long.valueOf(distributorId), itemCode, Long.valueOf(salesGroupId), state).isPresent());
				modelView.addObject("matchAC2", ingredientService.existsByInDistribtorPkidAndInDistribtorItemCode(Long.valueOf(distributorId), itemCode)
						|| StringUtil.isAllEmpty(ingredientId)?false:ingredientService.existsByPkId(Long.valueOf(ingredientId)));
				boolean matchDistributorBrandId = (this.brandService.findByDistributorBrandId(distributorBrandId) != null);
				modelView.addObject("matchAC3", matchDistributorBrandId);
				modelView.addObject("matchAC4", this.ingredientService.findByMpc(mpc).isPresent());
				
				if (StringUtil.isNotEmpty(distributorBrandId) && !matchDistributorBrandId) {
					modelView.addObject("error", "Distributor brand ID exists in the import but has no match in database!");
				}
				
				modelView.addObject("distributorId", distributorId);
				modelView.addObject("itemCode",itemCode);
				modelView.addObject("salesGroupId",salesGroupId);
				modelView.addObject("state",state);
				modelView.addObject("ingredientId",ingredientId);
				modelView.addObject("distributorBrandId",distributorBrandId);
				modelView.addObject("mpc",mpc);
			}
		} catch (Exception e) {
			modelView.addObject("error", e.getMessage());
		}
		return modelView;
	}
	
}
