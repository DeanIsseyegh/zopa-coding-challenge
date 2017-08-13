package dean.zopa;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class LoanCalculator {

	public Map<String,BigDecimal> invest(BigDecimal moneyToInvest, List<Lender> lenders) {

		return null;
	}

	/**
	 * Prototype:
	 *
	 * // Create map of lender rate and their name
	 * Map<String, BigDecimal> lenderRates = new HashMap<>();
	 lenders.forEach((lender) -> {
	 lenderRates.put(lender.name, lender.rate);
	 }
	 );

	 // Calculate total rate
	 BigDecimal totalRate = BigDecimal.ZERO;
	 for (String lenderName : lenderRates.keySet()) {
	 totalRate = totalRate.add(lenderRates.get(lenderName));
	 }

	 // Calculate amount to be borrowed from each lender
	 Map<String, BigDecimal> investmentPerLender = new HashMap<>();
	 for (String lenderName : lenderRates.keySet()) {
	 BigDecimal rate = lenderRates.get(lenderName);
	 BigDecimal investmentRatio = rate.divide(totalRate, BigDecimal.ROUND_DOWN);
	 investmentPerLender.put(lenderName, investmentRatio.multiply(moneyToInvest));
	 }


	 return investmentPerLender;
	 */

}
