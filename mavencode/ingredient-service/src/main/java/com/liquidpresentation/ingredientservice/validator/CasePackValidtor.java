package com.liquidpresentation.ingredientservice.validator;

import com.liquidpresentation.ingredientservice.model.Price;
import org.apache.commons.lang.StringUtils;

public class CasePackValidtor extends PriceValidatorAdaptor implements PriceValidator {
    @Override
    public ValidationResult validate(Price price) {
        try {
            if (StringUtils.isEmpty(price.getCsvPrCasePack())) {
                price.setPrCasePack(null);
            } else {
                String csvCasePack = price.getCsvPrCasePack().replace(",", "");
                Integer caskPack = Integer.valueOf(csvCasePack);
                price.setPrCasePack(caskPack);
            }
        } catch (Exception e) {
            return new ValidationResult(true, price, " Case Pack is not valid");
        }
        return this.resultSucess(price);
    }
}