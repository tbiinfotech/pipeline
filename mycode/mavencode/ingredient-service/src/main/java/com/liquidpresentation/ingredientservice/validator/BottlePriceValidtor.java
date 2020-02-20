package com.liquidpresentation.ingredientservice.validator;

import com.liquidpresentation.ingredientservice.model.Price;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;

public class BottlePriceValidtor extends PriceValidatorAdaptor implements PriceValidator {
    @Override
    public ValidationResult validate(Price price) {
        try {
            if (StringUtils.isEmpty(price.getCsvPrBottlePrice())) {
                price.setPrBottlePrice(null);
            } else {
                String csvBottlePrice = price.getCsvPrBottlePrice().replace(",", "");
                Double.valueOf(csvBottlePrice);
                price.setPrBottlePrice(new BigDecimal(csvBottlePrice));
            }
        } catch (Exception e) {
            return new ValidationResult(true, price, " Bottle Price is not valid");
        }

        return this.resultSucess(price);
    }
}
