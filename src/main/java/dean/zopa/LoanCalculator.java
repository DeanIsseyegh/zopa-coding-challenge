package dean.zopa;

import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoanCalculator {

	private LoanAlgorithm loanAlgorithm;
	private List<Map<Lender, MonetaryAmount>> amountsToBorrowPerLenderList = new ArrayList<>();

	public LoanCalculator(LenderPool lenderPool) {
		loanAlgorithm  = new LoanAlgorithm(lenderPool);
	}

	public List<Map<Lender, MonetaryAmount>> quote(MonetaryAmount amountToBorrow) {
		Map<Lender, BigDecimal> lenderRatios = loanAlgorithm.calcLenderRatios();
		Map<Lender, MonetaryAmount> amountsToBorrowPerLender = loanAlgorithm.calcAmountsToBorrowPerLender(amountToBorrow, lenderRatios);
		MonetaryAmount leftOverAmount = loanAlgorithm.modifyAmountsToBorrowAndReturnLeftOverAmount(amountsToBorrowPerLender);
		amountsToBorrowPerLenderList.add(amountsToBorrowPerLender);
		System.out.println(leftOverAmount);
		if (leftOverAmount.isGreaterThan(Money.of(0.001, Config.CURRENCY))) {
			quote(leftOverAmount);
		}
		return amountsToBorrowPerLenderList;
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
