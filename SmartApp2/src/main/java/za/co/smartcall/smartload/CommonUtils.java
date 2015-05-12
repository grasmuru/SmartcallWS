package za.co.smartcall.smartload;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class CommonUtils {
	
    public static String currencyFormat(BigDecimal n) {
	     return NumberFormat.getCurrencyInstance().format(n);
	 }
   
   public static String discountedPrice(String price,BigDecimal discount) {
   	 try {
			Double priceN = NumberFormat.getCurrencyInstance().parse(price).doubleValue();;
			 BigDecimal priceDecimal = new BigDecimal(priceN);
			 BigDecimal costPrice =priceDecimal.multiply((new BigDecimal(1).subtract(discount.divide(new BigDecimal(100)))));
			 return NumberFormat.getCurrencyInstance().format(costPrice);
		} catch (Exception e) {
			return "-";
		}
	 }
   

}
